package com.gy.listener.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.gy.listener.MyApplication;

public class Helpers {

    // region Constants

    private static final String SP_FILE =
            "listener_sp";

    private static final String SP_RECORDS_LAST_UPDATE =
            "last_update";

    // endregion

    // region SharedPreferences

    public static void setLocalLastUpdated(Long timestamp) {
        SharedPreferences.Editor editor =
                MyApplication.getAppContext().getSharedPreferences(SP_FILE, Context.MODE_PRIVATE).edit();
        editor.putLong(SP_RECORDS_LAST_UPDATE, timestamp);
        editor.apply();
    }

    public static Long getLocalLastUpdated() {
        SharedPreferences sp =
                MyApplication.getAppContext().getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
        return sp.getLong(SP_RECORDS_LAST_UPDATE, 0);
    }

    // endregion
}
