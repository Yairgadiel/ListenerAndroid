package com.gy.listener.model.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersConverter {

    @TypeConverter
    public String fromUsersList(List<String> records) {
        if (records == null) {
            return (null);
        }

        StringBuilder json = new StringBuilder();
        if (!records.isEmpty()) {
            for (int i = 0; i < records.size(); i++) {
                String userId = records.get(i);

                json.append(userId);

                // Separate records with ','
                if (i < records.size() - 1) {
                    json.append(",");
                }
            }
        }

        return json.toString();
    }

    @TypeConverter
    public List<String> toUsersList(String recordsStr) {
        List<String> userIds = null;

        if (recordsStr != null) {
            userIds = new ArrayList<>();

            if (!recordsStr.isEmpty()) {
                // Separate the content from the type
                String[] usersSplit = recordsStr.split(",");

                userIds.addAll(Arrays.asList(usersSplit));
            }
        }

        return userIds;
    }
}