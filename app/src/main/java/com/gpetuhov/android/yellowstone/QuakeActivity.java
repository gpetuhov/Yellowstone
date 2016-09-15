package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


// Activity for details of earthquake
public class QuakeActivity extends SingleFragmentActivity {

    // Key for extra data in intent
    private static final String EXTRA_QUAKE_ID = "quake_id_extra_data";

    // Return new intent with extra data to start this activity
    public static Intent newIntent(Context packageContext, String quakeId) {

        // Create explicit intent to start this activity
        Intent intent = new Intent(packageContext, QuakeActivity.class);

        // Put ID of earthquake as extra data in intent
        intent.putExtra(EXTRA_QUAKE_ID, quakeId);

        return intent;
    }

    // Return new fragment for details of earthquake
    @Override
    protected Fragment createFragment() {

        // Get earthquake ID from extra data of the received intent, that started this activity
        String quakeId = getIntent().getStringExtra(EXTRA_QUAKE_ID);

        // Create new fragment with details of earthquakes
        // Earthquake ID from the intent is passed to the instance of QuakeFragment
        // as a fragment argument
        return QuakeFragment.newInstance(quakeId);
    }

}
