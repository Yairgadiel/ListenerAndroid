package com.gy.listener.model;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gy.listener.model.db.DatabaseHelper;
import com.gy.listener.model.events.IOnCompleteListener;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.model.firebase.FirebaseModel;
import com.gy.listener.model.items.records.Record;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.items.users.User;
import com.gy.listener.utilities.Helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordsListsRepository {

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

        recordsList.getUserIds().add(_loggedUser.getValue().getId());

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
                        currList.getLastUpdated());

                break;
            }
        }

        return recordsList;
    }

    // endregion
}
