package com.gy.listener.model.items.records;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.gy.listener.model.db.DataConverter;
import com.gy.listener.model.db.DbContract;
import com.gy.listener.model.items.IJsonConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity(tableName = DbContract.RECORDS_LIST_TABLE)
public class RecordsList implements Serializable, IJsonConverter, Comparable {

    // region Constants

    public static final String ID =
            "Id";

    public static final String NAME =
            "Name";

    public static final String DETAILS =
            "Details";

    public static final String TYPE =
            "Type";

    public static final String RECORDS =
            "Records";

    public static final String DATE_CREATED =
            "DateCreated";

    public static final String LAST_UPDATED =
            "LastUpdated";

    public static final String USERS =
            "Users";

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

    @ColumnInfo(name = "list_date_created")
    private long _dateCreated;

    @ColumnInfo(name = "list_update")
    private Long _lastUpdated;

    @Ignore
    private List<String> _userIds;

    // endregion

    // region C'tor

    public RecordsList(@NonNull String id, @NonNull String name, @Nullable String details, ListType listType, List<Record> records, long dateCreated, Long lastUpdated) {
        this._id = id;
        this._name = name;
        this._details = details;
        this._listType = listType;
        this._records = records;
        this._dateCreated = dateCreated;
        this._lastUpdated = lastUpdated;
    }

    @Ignore
    public RecordsList(@NonNull String id,
                       @NonNull String name,
                       @Nullable String details,
                       ListType listType,
                       List<Record> records,
                       long dateCreated,
                       Long lastUpdated,
                       List<String> userIds) {
        this._id = id;
        this._name = name;
        this._details = details;
        this._listType = listType;
        this._records = records;
        this._dateCreated = dateCreated;
        this._lastUpdated = lastUpdated;
        this._userIds = userIds;
    }

    @Ignore
    public RecordsList(@NonNull String id, @NonNull String name, @Nullable String details, ListType listType) {
        this._id = id;
        this._name = name;
        this._details = details;
        this._listType = listType;
        this._records = new ArrayList<>();
        this._dateCreated = System.currentTimeMillis();
        this._lastUpdated = 0L;
        this._userIds = new ArrayList<>();
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

    public void setRecords(List<Record> records) {
        _records = records;
    }

    public long getDateCreated() {
        return _dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        _dateCreated = dateCreated;
    }

    public Long getLastUpdated() {
        return _lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        _lastUpdated = lastUpdated;
    }

    public List<String> getUserIds() {
        if (_userIds == null) {
            _userIds = new ArrayList<>();
        }

        return _userIds;
    }

    public void setUserIds(List<String> userIds) {
        _userIds = userIds;
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
        json.put(TYPE, _listType.ordinal());
        json.put(RECORDS, recordsConverter.fromRecordsList(_records));
        json.put(DATE_CREATED, _dateCreated);
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        json.put(USERS, _userIds);

        return json;
    }
    
    public static RecordsList create(@NonNull Map<String, Object> json) {
        DataConverter recordsConverter = new DataConverter();

        RecordsList recordsList = new RecordsList(
                (String) json.get(ID),
                (String) json.get(NAME),
                (String) json.get(DETAILS),
                ListType.values()[((Long) json.get(TYPE)).intValue()],
                recordsConverter.toRecordsList((String) json.get(RECORDS)),
                (long) json.get(DATE_CREATED),
                json.get(LAST_UPDATED) == null ? 0 : ((Timestamp) json.get(LAST_UPDATED)).getSeconds(),
                (List<String>) json.get(USERS));

        return recordsList;
    }

    // endregion

    // region Comparable

    @Override
    public int compareTo(Object o) {
        return ((int) (((RecordsList) o)._dateCreated - _dateCreated));
    }

    // endregion

}
