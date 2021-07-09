package com.gy.listener.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.gy.listener.myLists.models.RecordsList;

import java.util.List;

@Dao
public interface IRecordsListDAO {
    @Insert
    void insertAll(RecordsList... recordsLists);

    @Delete
    void delete(RecordsList recordsList);

    @Query("SELECT * FROM " + DbContract.RECORDS_LIST_TABLE)
    LiveData<List<RecordsList>> getAll();

    @Query("SELECT * FROM " + DbContract.RECORDS_LIST_TABLE + " WHERE _id = :id ")
    List<RecordsList> getById(String id);

}
