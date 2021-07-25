package com.gy.listener.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.gy.listener.model.RecordsListsRepository;
import com.gy.listener.model.events.IValidator;
import com.gy.listener.model.items.records.RecordsList;
import com.gy.listener.model.events.IOnCompleteListener;

import java.util.List;

public class RecordsListsViewModel extends ViewModel {
    private LiveData<List<RecordsList>> _data;

    public RecordsListsViewModel() {
    }

    public LiveData<List<RecordsList>> getData(IOnCompleteListener listener) {
        if (_data == null) {
            _data = RecordsListsRepository.getInstance().getAllLists(listener);
        }
        else {
            // Nothing to load
            listener.onComplete(true);
        }

        return _data;
    }

    public RecordsList getRecordsListById(String id) {
        return RecordsListsRepository.getInstance().getCopyRecordsListById(id);
    }

    public void setRecordsList(RecordsList recordsList, IOnCompleteListener listener) {
        RecordsListsRepository.getInstance().addRecordsList(recordsList, listener);
    }

    public void refreshData(IOnCompleteListener listener) {
        RecordsListsRepository.getInstance().getAllLists(listener);
    }

    public void isIdAvailable(String id, IValidator validator) {
        RecordsListsRepository.getInstance().isIdAvailable(id, validator);
    }

}
