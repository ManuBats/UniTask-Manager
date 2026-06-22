package com.example.unitask_manager.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.unitask_manager.utils.Constants;

public class TokenManager {

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREF_SESION, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(Constants.PREF_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(Constants.PREF_TOKEN, null);
    }

    public void clearToken() {
        prefs.edit().remove(Constants.PREF_TOKEN).apply();
    }

    public boolean hasToken() {
        return getToken() != null && !getToken().isEmpty();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
