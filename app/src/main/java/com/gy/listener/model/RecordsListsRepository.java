package com.gy.listener.model;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gy.listener.model.firestore.FirestoreModel;
import com.gy.listener.model.items.Record;
import com.gy.listener.model.items.RecordsList;
import com.gy.listener.model.events.IOnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordsListsRepository {

    // region Members

    private final ExecutorService _executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<RecordsList>> _lists;

    // endregion

    // region Singleton

    private static RecordsListsRepository _instance;

    private RecordsListsRepository() {
        _lists = new MutableLiveData<>(new ArrayList<>());
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
        FirestoreModel.getInstance().setRecordsList(recordsList, listener);

//        _executorService.execute(() -> {
//            DatabaseHelper.db.recordsListDAO().insertAll(recordsList);
//        });
    }

    public void updateRecordsList(RecordsList recordsList, IOnCompleteListener listener) {
//        _executorService.execute(() -> {
//            DatabaseHelper.db.recordsListDAO().update(recordsList);
//        });
        FirestoreModel.getInstance().setRecordsList(recordsList, listener);
    }

    public LiveData<List<RecordsList>> getAllLists(/*IOnCompleteListener listener*/) {
//        if (_lists == null) {
//             Loading...
//            _lists = DatabaseHelper.db.recordsListDAO().getAll();
//             ObserveForever -> loaded
//        }

        FirestoreModel.getInstance().getAllRecordsList(data -> {
            /*listener.onComplete(data != null);*/
            _lists.setValue(data);
        });

        return _lists;
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

    // endregion
}
