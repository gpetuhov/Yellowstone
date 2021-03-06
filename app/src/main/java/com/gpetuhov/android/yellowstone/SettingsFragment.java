package com.gpetuhov.android.yellowstone;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;


// Fragment for application settings.
// Extends PreferenceFragmentCompat from the support library.
// Compatible with other fragments from the support library.
// Implements Preference.OnPreferenceChangeListener to update UI summary when preferences change.
public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.pref_general, s);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.

        // Get magnitude preference key, find preference with this key
        // and bind this preference (magnitude preference) summary to value
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_magnitude_key)));

        // Get refresh quake list preference key, find preference with this key
        // and bind this preference (refresh quake list preference) summary to value
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_refresh_quake_key)));
    }


    // Attaches a listener so the summary is always updated with the preference value.
    // Also fires the listener once, to initialize the summary (so it shows up before the value
    // is changed.)
    private void bindPreferenceSummaryToValue(Preference preference) {

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Display current preference value in Summary
        // (get default SharedPreferences with context of the app,
        // get string with the key from the preference from SharedPreferences
        // and update preference summary with this string (current value of the preference)).
        displaySummary(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .getString(preference.getKey(), ""));
    }


    // Display value of the preference in the preference Summary
    private void displaySummary(Preference preference, Object value) {

        // Cast new value of the preference to String
        String stringValue = value.toString();

        // If the preference is instance of ListPreference
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).

            // Cast preference to ListPreference
            ListPreference listPreference = (ListPreference) preference;

            // Find index of the new value in values array
            // (ListPreference stores entries and values in 2 separate arrays)
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if (prefIndex >= 0) {
                // Get entry with the index of new value from entries array
                // and set this entry as a new summary of the preference.
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }

    // Method is called when a preference changes
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        // Display current preference value in Summary
        displaySummary(preference, value);

        // If the preference key equals to refresh quake list preference key
        if (preference.getKey().equals(getString(R.string.pref_refresh_quake_key))) {
            // Set quake notifications service (start or stop depending on settings value)
            QuakePollService.setServiceAlarm(getActivity());
        }

        return true;
    }

}