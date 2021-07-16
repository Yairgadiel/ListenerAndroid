package com.gy.listener.model.firestore;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gy.listener.model.items.RecordsList;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IOnRecordsListsFetchListener;

import java.util.ArrayList;
import java.util.List;

public class FirestoreModel {

    // region Constants

    private static final String RECORDS_LIST_COLLECTION = "records_list";

    // endregion

    // region Members

    private FirebaseFirestore _firestoreDb;

    // endregion

    // region Singleton

    private static FirestoreModel _instance = null;

    private FirestoreModel() {
        _firestoreDb = FirebaseFirestore.getInstance();
    }

    public static FirestoreModel getInstance() {
        if (_instance == null) {
            _instance = new FirestoreModel();
        }

        return _instance;
    }

    // endregion

    // region Public Methods

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

                    if (task.isSuccessful()) {
                        data = new ArrayList<>(task.getResult().size());

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            data.add(RecordsList.create(doc.getData()));
                        }
                    }

                    listener.onFetch(data);
                });
    }

    // endregion
}
