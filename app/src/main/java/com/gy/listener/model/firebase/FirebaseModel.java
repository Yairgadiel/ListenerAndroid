package com.gy.listener.model.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IOnImageLoadedListener;
import com.gy.listener.model.events.IOnImageUploadedListener;
import com.gy.listener.model.events.IOnRecordsListsFetchListener;
import com.gy.listener.model.events.IOnUsersFetchListener;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.items.users.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FirebaseModel {

    // region Constants

    private static final String RECORDS_LIST_COLLECTION = "records_lists";

    private static final String USERS_COLLECTION = "users";

    private static final String IMAGES_STORAGE = "images";

    // endregion

    // region Members

    private final FirebaseFirestore _firestoreDb;
    private final FirebaseAuth _firebaseAuth;
    private final FirebaseStorage _firebaseStorage;

    // endregion

    // region Singleton

    private static FirebaseModel _instance = null;

    private FirebaseModel() {
        _firestoreDb = FirebaseFirestore.getInstance();
        _firebaseAuth = FirebaseAuth.getInstance();
        _firebaseStorage = FirebaseStorage.getInstance();
    }

    public static FirebaseModel getInstance() {
        if (_instance == null) {
            _instance = new FirebaseModel();
        }

        return _instance;
    }

    // endregion

    // region Firestore

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

    /**
     * This method gets all users having the given field matching the given value
     */
    public void getAllUsersWithField(String field, String value, IOnUsersFetchListener listener) {
        _firestoreDb.collection(USERS_COLLECTION)
                .whereEqualTo(field, value)
                .get()
                .addOnCompleteListener(task -> {
                    List<User> data = null;

                    if (task.isSuccessful() && task.getResult() != null) {
                        data = new ArrayList<>(task.getResult().size());

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            data.add(doc.toObject(User.class));
                        }
                    }

                    listener.onFetch(data);
                });
    }

    public void getAllUsers(IOnUsersFetchListener listener) {
        _firestoreDb.collection(USERS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    List<User> data = null;

                    if (task.isSuccessful() && task.getResult() != null) {
                        data = new ArrayList<>(task.getResult().size());

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            data.add(doc.toObject(User.class));
                        }
                    }

                    listener.onFetch(data);
                });
    }

    // endregion

    // endregion

    // region Authentication

    @Nullable
    public User getLoggedUser() {
        FirebaseUser fbUser = _firebaseAuth.getCurrentUser();
        User loggedUser = null;

        if (fbUser != null) {
            loggedUser = new User(fbUser.getUid(), fbUser.getDisplayName(), fbUser.getEmail());
        }

        return loggedUser;
    }

    public void signUp(String name, String email, String password, IOnCompleteListener listener) {
        _firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        listener.onComplete(false);
                    }
                    else {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = _firebaseAuth.getCurrentUser();

                        if (user == null) {
                            listener.onComplete(false);
                        }
                        else {
                            // Setting the new user's name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                                        if (task.isSuccessful()) {
                                            listener.onComplete(true);
                                        }
                                        else {
                                            // If unable to set the user's name for some reason - delete it
                                            user.delete();
                                            listener.onComplete(false);
                                        }
                                    });
                        }
                    }
                });
    }

    public void signIn(String email, String password, IOnCompleteListener listener) {
        _firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> listener.onComplete(task.isSuccessful()));
    }

    public void signOut() {
        _firebaseAuth.signOut();
    }

    // endregion

    // region Storage

    public void uploadImage(String name, Bitmap img, IOnImageUploadedListener listener) {
        final StorageReference imagesRef = _firebaseStorage.getReference().child(IMAGES_STORAGE).child(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(e -> {
            listener.onUploaded(null);
            e.printStackTrace();
        })
        .addOnSuccessListener(taskSnapshot -> {
            loadImage(name, listener);
        });
    }

    public void loadImage(String name, IOnImageUploadedListener listener) {
        final StorageReference imagesRef = _firebaseStorage.getReference().child(IMAGES_STORAGE).child(name);
        imagesRef.getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onUploaded(uri.toString()))
                .addOnFailureListener(exc -> {
                    listener.onUploaded(null);
                    exc.printStackTrace();
                });
    }

    public void deleteImage(String name, IOnCompleteListener listener) {
        // Create a storage reference from our app
        StorageReference storageRef = _firebaseStorage.getReference();
        StorageReference imageRef = storageRef.child(IMAGES_STORAGE).child(name);

        // Delete the file
        imageRef.delete().addOnSuccessListener(aVoid -> listener.onComplete(true))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    listener.onComplete(false);
                });
    }

    // endregion
}
