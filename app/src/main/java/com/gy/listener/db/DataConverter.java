package com.gy.listener.db;

import androidx.room.TypeConverter;

import com.gy.listener.model.items.CheckedRecord;
import com.gy.listener.model.items.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataConverter {
    private static final String TYPE_SEPARATOR = "@";
    private static final String LIST_OPENER = "[";
    private static final String LIST_CLOSER = "]";
    private static final String RECORD_SEPARATOR = "\\x7D\\x2C\\x7B"; // },{
    private static final String ENTRY_SEPARATOR = ", ";
    private static final String VALUE_SEPARATOR = "=";
    private static final int PRE_POST_FIX_LEN = 2; // [{ or }]

    @TypeConverter
    public String fromRecordsList(List<Record> records) {
        if (records == null) {
            return (null);
        }

        StringBuilder json = new StringBuilder();
        if (!records.isEmpty()) {
            json.append(LIST_OPENER);

            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);

                json.append(record.toJson());

                // Separate records with ','
                if (i < records.size() - 1) {
                    json.append(",");
                }
            }

            // At last, add the records' type
            json.append(LIST_CLOSER).append(TYPE_SEPARATOR).append(records.get(0).getClass().getSimpleName());

            /*
             [{text: val, isChecked: true}, {text: val, isChecked: true}, {text: val, isChecked: true}]@CheckedRecord
             */
        }

        return json.toString();
    }

    @TypeConverter
    public List<Record> toRecordsList(String recordsStr) {
        List<Record> records = null;

        if (recordsStr != null) {
            records = new ArrayList<>();

            if (!recordsStr.isEmpty()) {
                // Separate the content from the type
                String[] typeSplit = recordsStr.split(TYPE_SEPARATOR);

                // Exclude prefix and postfix
                String noPrePostFix = typeSplit[0].substring(PRE_POST_FIX_LEN, typeSplit[0].length() - PRE_POST_FIX_LEN);

                // Split all the records
                String[] splittedRecords = noPrePostFix.split(RECORD_SEPARATOR);

                for (String splittedRecord : splittedRecords) {
                    Map<String, Object> record = new HashMap<>();

                    String[] entries = splittedRecord.split(ENTRY_SEPARATOR);

                    for (String entry : entries) {
                        String[] splittedEntry = entry.split(VALUE_SEPARATOR);
                        record.put(splittedEntry[0], splittedEntry[1]);
                    }

                    // Add a new record to the redords list according to its type
                    if (typeSplit[1].equals(CheckedRecord.class.getSimpleName())) {
                        records.add(CheckedRecord.create(record));
                    }
                    // TODO add implementation for other records
                }
            }
        }

        return records;
    }
}