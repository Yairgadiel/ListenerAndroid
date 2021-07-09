package com.gy.listener.myLists;

import androidx.lifecycle.LiveData;

import com.gy.listener.db.DatabaseHelper;
import com.gy.listener.myLists.models.RecordsList;

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

    public void addItemsList(RecordsList recordsList) {
        _executorService.execute(() -> {
            DatabaseHelper.db.itemsListDAO().insertAll(recordsList);
        });

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
