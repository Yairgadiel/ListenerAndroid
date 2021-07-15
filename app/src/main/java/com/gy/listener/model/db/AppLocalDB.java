package com.gy.listener.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.gy.listener.model.items.RecordsList;

@Database(entities = {RecordsList.class}, version = 1)
public abstract class AppLocalDB extends RoomDatabase {
    public abstract IRecordsListDAO recordsListDAO();
}
