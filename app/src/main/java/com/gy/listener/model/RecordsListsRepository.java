package com.gy.listener.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gy.listener.MyApplication;
import com.gy.listener.model.db.DatabaseHelper;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IOnImageLoadedListener;
import com.gy.listener.model.events.IOnImageUploadedListener;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.model.firebase.FirebaseModel;
import com.gy.listener.model.items.records.Record;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.items.users.User;
import com.gy.listener.utilities.Helpers;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordsListsRepository {

    // region Constants

    private static final String IMAGES_DIR_PATH =
            Environment.DIRECTORY_PICTURES + "/Listener";

    private static final File IMAGES_DIR =
            Environment.getExternalStoragePublicDirectory(IMAGES_DIR_PATH);

    // endregion

    // region Members

    private final ExecutorService _executorService = Executors.newSingleThreadExecutor();
    private final LiveData<List<RecordsList>> _lists = DatabaseHelper.db.recordsListDAO().getAll();
    private final MutableLiveData<User> _loggedUser;

    // endregion

    // region Singleton

    private static RecordsListsRepository _instance;

    private RecordsListsRepository() {
        _loggedUser = UsersRepository.getInstance().getLoggedUser();
    }

    public static RecordsListsRepository getInstance() {
        if (_instance == null) {
            _instance = new RecordsListsRepository();
        }

        return _instance;
    }

    // endregion

    // region Public Methods

    public void addRecordsList(RecordsList recordsList, IOnCompleteListener listener) {
        Log.d("LISTENER", "repo add list");

        _executorService.execute(() -> FirebaseModel.getInstance().setRecordsList(recordsList, isSuccess -> {
            // Not passing the received listener since we want to fire it only after
            // fully completing the task (we need to pull the records as well)
            Log.d("LISTENER", "repo add list is success " + isSuccess);

            // No point of continuing if the addition has failed
            if (isSuccess) {
                // Retrieving all the data and updating the local db, thus updating the UI
                getAllLists(listener);
            }
            else {
                listener.onComplete(false);
            }
        }));
    }

    public LiveData<List<RecordsList>> getAllLists(IOnCompleteListener listener) {
        Long localLastUpdate = Helpers.getLocalLastUpdated();

        Log.d("LISTENER", "repo getAllLists");

        // Get all updates from firesrore
        FirebaseModel.getInstance().getAllRecordsList(localLastUpdate, data -> _executorService.execute(() -> {
            if (data != null) {
                Log.d("LISTENER", "repo getAllLists data no null");

                Long lastUpdate = 0L;

                for (RecordsList recordsList : data) {
                    // Checking if the current user has deleted = left the list
                    if (_loggedUser.getValue() == null || !recordsList.getUserIds().contains(_loggedUser.getValue().getId())) {
                        DatabaseHelper.db.recordsListDAO().delete(recordsList);
                    }
                    else {
                        // Update DB with the new list
                        DatabaseHelper.db.recordsListDAO().insertAll(recordsList);

                        // Updating the records list in the local collection since ROOM f*cking refuses to do so
                        for (RecordsList list : _lists.getValue()) {
                            if (list.getId().equals(recordsList.getId())) {
                                list.setRecords(recordsList.getRecords());
                                list.setUserIds(recordsList.getUserIds());

                                break;
                            }
                        }
                    }

                    // Find last last update timestamp
                    if (lastUpdate < recordsList.getLastUpdated()) {
                        lastUpdate = recordsList.getLastUpdated();
                    }
                }

                Helpers.setLocalLastUpdated(lastUpdate);
            }

            Collections.sort(_lists.getValue());

            listener.onComplete(data != null);
        }));

        // The data is updated automatically via ROOM on each insert
        return _lists;
    }

    public void isIdAvailable(String id, IValidator validator) {
        FirebaseModel.getInstance().getAllRecordsWithField(RecordsList.ID, id, data -> validator.isValid(data == null || data.isEmpty()));
    }

    /**
     * Gets a copy of records list given the original's id.
     * Not returning live data since we don't want the DB to be automatically updated
     */
    @Nullable
    public RecordsList getCopyRecordsListById(String id) {
        RecordsList recordsList = null;

        for (RecordsList currList : _lists.getValue()) {
            if (currList.getId().equals(id)) {
                // Copy records
                List<Record> copyRecords = new ArrayList<>();

                for (Record record : currList.getRecords()) {
                    copyRecords.add(record.getClone());
                }

                recordsList = new RecordsList(currList.getId(),
                        currList.getName(),
                        currList.getDetails(),
                        currList.getListType(),
                        copyRecords,
                        currList.getDateCreated(),
                        currList.getLastUpdated(),
                        currList.getUserIds());

                break;
            }
        }

        return recordsList;
    }

    // region Images

    public void saveRecordAttachment(String name, Uri imageUri, IOnImageUploadedListener listener) {
        _executorService.execute(() -> {
            Bitmap imageBitmap = null;

            try {
                imageBitmap = Picasso.get().load(imageUri).resize(500, 500).centerCrop().get();
            }
            catch (IOException e) {
                Log.d("LISTENER", "Failed to extract bitmap");
                e.printStackTrace();
            }

            if (imageBitmap == null) {
                listener.onUploaded(null);
            }
            else {
                // Creating final instance accessible in the lambda
                final Bitmap finalImageBitmap = imageBitmap;
                FirebaseModel.getInstance().uploadImage(name,
                        imageBitmap,
                        downloadUrl -> {
                            String localName = getLocalImageFileName(downloadUrl);
                            Log.d("LISTENER", "cache image: " + localName);

                            // Synchronously save image locally
                            saveImageToFile(finalImageBitmap, localName);

                            listener.onUploaded(localName);
                        });
            }
        });
    }

    public void loadRecordAttachment(final String fileName, final IOnImageLoadedListener listener) {
        // First try to find the image on the device
        Bitmap image = loadImageFromFile(fileName);

        if (image != null) {
            Log.d("LISTENER","Reading cache image: " + fileName);
            listener.onLoaded(image);
        }
        else {
            _executorService.execute(() -> {
                // Image not found - try downloading it from parse
                FirebaseModel.getInstance().loadImage(noPostfix(fileName), loadedImgUrl -> {
                    _executorService.execute(() -> {
                        try {
                            saveLocallyFromRemoteUrl(loadedImgUrl);

                            // Return the image using the listener
                            listener.onLoaded(Picasso.get().load(loadedImgUrl).get());
                        }
                        catch (IOException e) {
                            listener.onLoaded(null);
                            e.printStackTrace();
                        }
                    });
                });
            });
        }
    }

    public void deleteRecordAttachment(String name, IOnCompleteListener listener) {
        _executorService.execute(() -> {
            FirebaseModel.getInstance().deleteImage(noPostfix(name), listener);
            deleteLocalFile(name);
        });
    }

    private void saveLocallyFromRemoteUrl(@Nullable String loadedImgUrl) {
        try {
            if (loadedImgUrl != null) {
                // Save the image locally
                String localFileName = getLocalImageFileName(loadedImgUrl);

                Log.d("LISTENER", "Save image to cache: " + localFileName);

                Bitmap imageBitmap = Picasso.get().load(loadedImgUrl).get();
                saveImageToFile(imageBitmap, localFileName);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a given image to a local file given the image (bitmap) and the file's desired name
     */
    private void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
        try {
            if (!IMAGES_DIR.exists()) {
                IMAGES_DIR.mkdirs();
            }

            File imageFile = new File(IMAGES_DIR, imageFileName);
            imageFile.createNewFile();
            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            addImgToGallery(imageFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a file given it's name
     */
    private void deleteLocalFile(String fileName) {
        try {
            if (IMAGES_DIR.exists()) {
                File fileToDelete = new File(IMAGES_DIR, fileName);
                fileToDelete.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadImageFromFile(String fileName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(IMAGES_DIR.getPath() + "/" + fileName, options);
    }

    /**
     * Add the picture to the gallery so we don't need to manage the cache size
     * @param imageFile the file to add to gallery
     */
    private void addImgToGallery(File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getAppContext().sendBroadcast(mediaScanIntent);
    }

    private String getLocalImageFileName(String url) {
        return URLUtil.guessFileName(url, null, null);
    }

    private String noPostfix(String str) {
        return str.substring(0, str.lastIndexOf("."));
    }

    // endregion

    // endregion
}
