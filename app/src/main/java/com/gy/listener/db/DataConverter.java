package com.gy.listener.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gy.listener.myLists.items.Record;

import java.lang.reflect.Type;
import java.util.List;

public class DataConverter {
    @TypeConverter
    public String fromRecordsList(List<Record> records) {
        if (records == null) {
            return (null);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Record>>() {}.getType();
        String json = gson.toJson(records, type);
        return json;
    }

    @TypeConverter
    public List<Record> toRecordsList(String countryLangString) {
        if (countryLangString == null) {
            return (null);
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<Record>>() {}.getType();
        List<Record> records = gson.fromJson(countryLangString, type);
        return records;
    }
}