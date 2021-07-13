package com.gy.listener.myLists;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.gy.listener.myLists.models.RecordsList;

import java.util.List;

public class MyListsViewModel extends ViewModel {
    private final LiveData<List<RecordsList>> _data;

    public MyListsViewModel() {
        _data = MyListsRepository.getInstance().getAllLists();
    }

    public LiveData<List<RecordsList>> getData() {
        return _data;
    }

    public RecordsList getRecordsListById(String id) {
        return MyListsRepository.getInstance().getCopyRecordsListById(id);
    }

    public void addRecordsList(RecordsList recordsList) {
        MyListsRepository.getInstance().addRecordsList(recordsList);
    }

    public void updateRecordsList(RecordsList recordsList) {
        MyListsRepository.getInstance().updateRecordsList(recordsList);
    }
}
