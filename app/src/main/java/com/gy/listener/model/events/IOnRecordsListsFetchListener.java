package com.gy.listener.model.events;

import androidx.annotation.Nullable;

import com.gy.listener.model.items.RecordsList;

import java.util.List;

public interface IOnRecordsListsFetchListener {
    void onFetch(@Nullable List<RecordsList> recordsLists);
}
