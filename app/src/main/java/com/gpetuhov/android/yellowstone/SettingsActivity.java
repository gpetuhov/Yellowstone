package com.gpetuhov.android.yellowstone;

import android.support.v4.app.Fragment;

// Activity with application settings
public class SettingsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }
}
