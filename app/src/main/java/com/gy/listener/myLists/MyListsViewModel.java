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
}
