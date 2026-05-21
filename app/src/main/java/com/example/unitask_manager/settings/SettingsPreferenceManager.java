package com.example.unitask_manager.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Persistencia local de preferencias de la sección Ajustes.
 */
public final class SettingsPreferenceManager {

    private static final String PREFS_NAME = "unitask_settings_prefs";

    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_DARK_THEME_ENABLED = "dark_theme_enabled";

    private static final String DEFAULT_USER_NAME = "María García";
    private static final String DEFAULT_EMAIL = "maria.garcia@universidad.edu";
    private static final String DEFAULT_PASSWORD = "";

    private final SharedPreferences preferences;

    public SettingsPreferenceManager(@NonNull Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, DEFAULT_USER_NAME);
    }

    public void setUserName(@NonNull String name) {
        preferences.edit().putString(KEY_USER_NAME, name.trim()).apply();
    }

    @NonNull
    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, DEFAULT_EMAIL);
    }

    public void setUserEmail(@NonNull String email) {
        preferences.edit().putString(KEY_USER_EMAIL, email.trim()).apply();
    }

    @NonNull
    public String getUserPassword() {
        return preferences.getString(KEY_USER_PASSWORD, DEFAULT_PASSWORD);
    }

    public void setUserPassword(@NonNull String password) {
        preferences.edit().putString(KEY_USER_PASSWORD, password.trim()).apply();
    }

    public boolean isNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }

    public boolean isDarkThemeEnabled() {
        return preferences.getBoolean(KEY_DARK_THEME_ENABLED, false);
    }

    public void setDarkThemeEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_DARK_THEME_ENABLED, enabled).apply();
    }

    @NonNull
    public static String computeInitials(@NonNull String fullName) {
        String trimmed = fullName.trim();
        if (trimmed.isEmpty()) {
            return "??";
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        String first = parts[0].substring(0, 1);
        String last = parts[parts.length - 1].substring(0, 1);
        return (first + last).toUpperCase();
    }
}
