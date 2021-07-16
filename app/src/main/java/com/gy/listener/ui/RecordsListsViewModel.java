package com.gy.listener.ui;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.gy.listener.model.RecordsListsRepository;
import com.gy.listener.model.items.RecordsList;
import com.gy.listener.model.events.IOnCompleteListener;

import java.util.List;

public class RecordsListsViewModel extends ViewModel {
    private final LiveData<List<RecordsList>> _data;

    public RecordsListsViewModel() {
        _data = RecordsListsRepository.getInstance().getAllLists(b-> Log.d("LISTENER", "Lists retrieval success: " + b));
    }

    public LiveData<List<RecordsList>> getData() {
        return _data;
    }

    public RecordsList getRecordsListById(String id) {
        return RecordsListsRepository.getInstance().getCopyRecordsListById(id);
    }

    public void setRecordsList(RecordsList recordsList, IOnCompleteListener listener) {
        RecordsListsRepository.getInstance().addRecordsList(recordsList, listener);
    }

//    public void updateRecordsList(RecordsList recordsList, IOnCompleteListener listener) {
//        RecordsListsRepository.getInstance().updateRecordsList(recordsList, listener);
//    }
}
