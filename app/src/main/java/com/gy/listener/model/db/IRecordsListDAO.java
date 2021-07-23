package com.gy.listener.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gy.listener.model.items.records.RecordsList;

import java.util.List;

@Dao
public interface IRecordsListDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(RecordsList... recordsLists);

    @Delete
    void delete(RecordsList recordsList);

    @Query("SELECT * FROM " + DbContract.RECORDS_LIST_TABLE)
    LiveData<List<RecordsList>> getAll();

}
