package com.gy.listener.model;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.gy.listener.model.db.DatabaseHelper;
import com.gy.listener.model.items.Record;
import com.gy.listener.model.items.RecordsList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordsListsRepository {
    private LiveData<List<RecordsList>> _lists;
    private ExecutorService _executorService = Executors.newSingleThreadExecutor();


    // region Singleton

    private static RecordsListsRepository _instance;

    private RecordsListsRepository() {
    }

    public static RecordsListsRepository getInstance() {
        if (_instance == null) {
            _instance = new RecordsListsRepository();
        }

        return _instance;
    }

    // endregion

    // region Public Methods

    public void addRecordsList(RecordsList recordsList) {
        _executorService.execute(() -> {
            DatabaseHelper.db.recordsListDAO().insertAll(recordsList);
        });
    }

    public void updateRecordsList(RecordsList recordsList) {
        _executorService.execute(() -> {
            DatabaseHelper.db.recordsListDAO().update(recordsList);
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
            _lists = DatabaseHelper.db.recordsListDAO().getAll();
            // ObserveForever -> loaded
        }

        return _lists;
    }

    // endregion
}
