package com.gy.listener.myLists;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.gy.listener.db.DatabaseHelper;
import com.gy.listener.myLists.models.Record;
import com.gy.listener.myLists.models.RecordsList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyListsRepository {
    private LiveData<List<RecordsList>> _lists;
    private ExecutorService _executorService = Executors.newSingleThreadExecutor();


    // region Singleton

    private static MyListsRepository _instance;

    private MyListsRepository() {
    }

    public static MyListsRepository getInstance() {
        if (_instance == null) {
            _instance = new MyListsRepository();
        }

        return _instance;
    }

    // endregion

    // region Public Methods

    public void addRecordsList(RecordsList recordsList) {
        _executorService.execute(() -> {
            DatabaseHelper.db.itemsListDAO().insertAll(recordsList);
        });
    }

    public void updateRecordsList(RecordsList recordsList) {
        _executorService.execute(() -> {
            DatabaseHelper.db.itemsListDAO().update(recordsList);
        });
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
                        copyRecords);

                break;
            }
        }

        return recordsList;
    }

    public LiveData<List<RecordsList>> getAllLists() {
        if (_lists == null) {
            // Loading...
            _lists = DatabaseHelper.db.itemsListDAO().getAll();
            // ObserveForever -> loaded
        }

        return _lists;
    }

    // endregion
}
