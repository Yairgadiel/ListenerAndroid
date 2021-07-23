package com.gy.listener.model.firebase;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IOnRecordsListsFetchListener;
import com.gy.listener.model.items.users.User;

import java.util.ArrayList;
import java.util.List;

public class FirebaseModel {

    // region Constants

    private static final String RECORDS_LIST_COLLECTION = "records_lists";

    private static final String USERS_COLLECTION = "users";

    // endregion

    // region Members

    private final FirebaseFirestore _firestoreDb;

    // endregion

    // region Singleton

    private static FirebaseModel _instance = null;

    private FirebaseModel() {
        _firestoreDb = FirebaseFirestore.getInstance();
    }

    public static FirebaseModel getInstance() {
        if (_instance == null) {
            _instance = new FirebaseModel();
        }

        return _instance;
    }

    // endregion

    // region Records Lists

    /**
     * This method adds/updates a records list
     * @param recordsList the records list to set
     * @param listener listener notifying of success/failure
     */
    public void setRecordsList(RecordsList recordsList, IOnCompleteListener listener) {
        _firestoreDb.collection(RECORDS_LIST_COLLECTION).document(recordsList.getId())
                .set(recordsList.toJson())
                .addOnSuccessListener(aVoid -> listener.onComplete(true))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    listener.onComplete(false);
                });
    }

    public void getAllRecordsList(Long since, IOnRecordsListsFetchListener listener) {
        _firestoreDb.collection(RECORDS_LIST_COLLECTION)
                .whereGreaterThanOrEqualTo(RecordsList.LAST_UPDATED, new Timestamp(since, 0))
                .get()
                .addOnCompleteListener(task -> {
                    List<RecordsList> data = null;

                    if (task.isSuccessful() && task.getResult() != null) {
                        data = new ArrayList<>(task.getResult().size());

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (doc != null) {
                                data.add(RecordsList.create(doc.getData()));
                            }
                        }
                    }

                    listener.onFetch(data);
                });
    }

    public void getAllRecordsWithField(String field, String id, IOnRecordsListsFetchListener listener) {
        _firestoreDb.collection(RECORDS_LIST_COLLECTION)
                .whereEqualTo(field, id)
                .get()
                .addOnCompleteListener(task -> {
                    List<RecordsList> data = null;

                    if (task.isSuccessful() && task.getResult() != null) {
                        data = new ArrayList<>(task.getResult().size());

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            data.add(RecordsList.create(doc.getData()));
                        }
                    }

                    listener.onFetch(data);
                });
    }

    // endregion

    // region Users

    /**
     * This method adds/updates a user
     * @param user the user to set
     * @param listener listener notifying of success/failure
     */
    public void setUser(User user, IOnCompleteListener listener) {
        _firestoreDb.collection(USERS_COLLECTION).document(user.getId())
                .set(user)
                .addOnSuccessListener(aVoid -> listener.onComplete(true))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    listener.onComplete(false);
                });
    }

    // endregion
}
