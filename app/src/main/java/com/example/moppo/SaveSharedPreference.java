package com.example.moppo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_USER_NAME = "username";
    static final String PREF_USER_ID = "userid";
    static final String PREF_USER_IDX = "useridx";

    static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setPrefUserData(Context context, String name, String id, int idx) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USER_NAME, name);
        editor.putString(PREF_USER_ID, id);
        editor.putInt(PREF_USER_IDX, idx);
        editor.commit();
    }

    public static String getPrefUserName(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_NAME, "");
    }

    public static String getPrefUserId(Context context) {
        return getSharedPreferences(context).getString(PREF_USER_ID, "");
    }

    public static int getPrefUserIdx(Context context) {
        return getSharedPreferences(context).getInt(PREF_USER_IDX, 0);
    }

    public static void clearPrefUserData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.commit();
    }
}