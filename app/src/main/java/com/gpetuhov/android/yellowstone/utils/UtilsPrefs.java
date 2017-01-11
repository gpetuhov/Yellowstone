package com.gpetuhov.android.yellowstone.utils;

import android.content.SharedPreferences;

// Utilities for SharedPreferences
public class UtilsPrefs {

    // Key for the most recent earthquake ID in SharedPreferences
    private static final String PREF_LAST_RESULT_ID = "last_result_id";

    // Key for the new quakes fetched flag in SharedPreferences
    private static final String PREF_NEW_QUAKES_FETCHED_FLAG = "new_quakes_fetched_flag";

    private SharedPreferences mSharedPreferences;

    public UtilsPrefs(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    // True, if there are no previously fetched quakes
    public boolean isPreviouslyFetchedQuakeNotExist() {
        // Get ID of the most recent earthquake from SharedPreferences
        String lastResultID = getLastResultId();

        // If ID of the most recent earthquake is null, return true
        return null == lastResultID;
    }

    // Return ID of the most recent fetched earthquake from SharedPreferences
    public String getLastResultId() {
        return mSharedPreferences.getString(PREF_LAST_RESULT_ID, null);
    }

    // Set new value for the ID of the most recent fetched earthquake in SharedPreferences
    public void setLastResultId(String lastResultId) {
        mSharedPreferences
                .edit()
                .putString(PREF_LAST_RESULT_ID, lastResultId)
                .apply();
    }

    // Return new quakes fetched flag from SharedPreferences
    public Boolean getNewQuakesFetchedFlag() {
        return mSharedPreferences.getBoolean(PREF_NEW_QUAKES_FETCHED_FLAG, false);
    }

    // Set new quakes fetched flag in SharedPreferences
    public void setNewQuakesFetchedFlag(Boolean flagValue) {
        mSharedPreferences
                .edit()
                .putBoolean(PREF_NEW_QUAKES_FETCHED_FLAG, flagValue)
                .apply();
    }
}
