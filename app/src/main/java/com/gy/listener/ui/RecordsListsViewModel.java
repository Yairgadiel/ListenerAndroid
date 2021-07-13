package com.gy.listener.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.gy.listener.model.RecordsListsRepository;
import com.gy.listener.model.items.RecordsList;

import java.util.List;

public class RecordsListsViewModel extends ViewModel {
    private final LiveData<List<RecordsList>> _data;

    public RecordsListsViewModel() {
        _data = RecordsListsRepository.getInstance().getAllLists();
    }

    public LiveData<List<RecordsList>> getData() {
        return _data;
    }

    public RecordsList getRecordsListById(String id) {
        return RecordsListsRepository.getInstance().getCopyRecordsListById(id);
    }

    public void addRecordsList(RecordsList recordsList) {
        RecordsListsRepository.getInstance().addRecordsList(recordsList);
    }

    public void updateRecordsList(RecordsList recordsList) {
        RecordsListsRepository.getInstance().updateRecordsList(recordsList);
    }
}
