package com.example.educar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManagement {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public SessionManagement(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void saveSession(String id) {
        editor.putString(SESSION_KEY, id).commit();
    }

    public String returnSession() {
        return sharedPreferences.getString(SESSION_KEY, "");
    }

    public void removeSession() {
        editor.putString(SESSION_KEY, "").commit();
    }


}
