package com.gpetuhov.android.yellowstone.utils;

import android.content.SharedPreferences;

// Utilities for SharedPreferences
public class UtilsPrefs {

    // Key for the most recent earthquake ID in SharedPreferences
    private static final String PREF_KEY_LAST_RESULT_ID = "last_result_id";

    public static final String NO_LAST_RESULT_ID = "no_last_result_id";

    // Key for the new quakes fetched flag in SharedPreferences
    private static final String PREF_KEY_NEW_QUAKES_FETCHED_FLAG = "new_quakes_fetched_flag";

    private SharedPreferences mSharedPreferences;

    public UtilsPrefs(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    // True, if there are no previously fetched quakes
    public boolean isPreviouslyFetchedQuakeNotExist() {
        String lastResultID = getLastResultId();
        return lastResultID.equals(NO_LAST_RESULT_ID);
    }

    // Return ID of the most recent fetched earthquake from SharedPreferences
    public String getLastResultId() {
        return getStringFromSharedPreferences(PREF_KEY_LAST_RESULT_ID, NO_LAST_RESULT_ID);
    }

    public String getStringFromSharedPreferences(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    // Set new value for the ID of the most recent fetched earthquake in SharedPreferences
    public void setLastResultId(String lastResultId) {
        putStringToSharedPreferences(PREF_KEY_LAST_RESULT_ID, lastResultId);
    }

    public void putStringToSharedPreferences(String key, String value) {
        mSharedPreferences
                .edit()
                .putString(key, value)
                .apply();
    }

    // Return new quakes fetched flag from SharedPreferences
    public boolean getNewQuakesFetchedFlag() {
        return getBooleanFromSharedPreferences(PREF_KEY_NEW_QUAKES_FETCHED_FLAG, false);
    }

    public boolean getBooleanFromSharedPreferences(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    // Set new quakes fetched flag in SharedPreferences
    public void setNewQuakesFetchedFlag(Boolean flagValue) {
        putBooleanToSharedPreferences(PREF_KEY_NEW_QUAKES_FETCHED_FLAG, flagValue);
    }

    public void putBooleanToSharedPreferences(String key, boolean value) {
        mSharedPreferences
                .edit()
                .putBoolean(key, value)
                .apply();
    }

    public int getIntFromSharedPreferences(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public void putIntToSharedPreferences(String key, int value) {
        mSharedPreferences
                .edit()
                .putInt(key, value)
                .apply();
    }
}
