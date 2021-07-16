package com.gy.listener.model.items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gy.listener.model.db.DataConverter;
import com.gy.listener.model.db.DbContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = DbContract.RECORDS_LIST_TABLE)
public class RecordsList implements Serializable, IJsonConverter {

    // region Constants

    private static final String ID =
            "Id";

    private static final String NAME =
            "Name";

    private static final String DETAILS =
            "Details";

    private static final String TYPE =
            "Type";

    private static final String RECORDS =
            "Records";

    // endsregion

    // region Members

    @PrimaryKey
    @NonNull
    private String _id;

    @ColumnInfo(name = "list_name")
    @NonNull
    private String _name;

    @ColumnInfo(name = "list_details")
    private String _details;

    @ColumnInfo(name = "list_type")
    private ListType _listType;

    @ColumnInfo(name = "list_records")
    @TypeConverters(DataConverter.class)
    private List<Record> _records;

    // endregion

    // region C'tor

    public RecordsList(@NonNull String id, @NonNull String name, @Nullable String details, ListType listType, List<Record> records) {
        this._id = id;
        this._name = name;
        this._details = details;
        this._listType = listType;
        this._records = records;
    }

    @Ignore
    public RecordsList(@NonNull String id, @NonNull String name, @Nullable String details, ListType listType) {
        this._id = id;
        this._name = name;
        this._details = details;
        this._listType = listType;
        this._records = new ArrayList<>();
    }

    // endregion

    // region Properties

    @NonNull
    public String getId() {
        return _id;
    }

    public void setId(@NonNull String _id) {
        this._id = _id;
    }

    @NonNull
    public String getName() {
        return _name;
    }

    public void setName(@NonNull String _name) {
        this._name = _name;
    }

    public String getDetails() {
        return _details;
    }

    public void setDetails(String details) {
        _details = details;
    }

    public ListType getListType() {
        return _listType;
    }

    public void setListType(ListType listType) {
        _listType = listType;
    }

    public List<Record> getRecords() {
        return _records;
    }

    void setRecords(List<Record> records) {
        _records = records;
    }

    // endregion

    // region IJsonConverter

    @Override
    public Map<String, Object> toJson() {
        DataConverter recordsConverter = new DataConverter();

        Map<String, Object> json = new HashMap<>();
        json.put(ID, _id);
        json.put(NAME, _name);
        json.put(DETAILS, _details);
        json.put(TYPE, (int) _listType.ordinal());
        json.put(RECORDS, recordsConverter.fromRecordsList(_records));

        return json;
    }
    
    public static RecordsList create(Map<String, Object> json) {
        DataConverter recordsConverter = new DataConverter();

        return new RecordsList(
                (String) json.get(ID),
                (String) json.get(NAME),
                (String) json.get(DETAILS),
                ListType.values()[((Long) json.get(TYPE)).intValue()],
                recordsConverter.toRecordsList((String) json.get(RECORDS)));
    }

    // endregion

}
