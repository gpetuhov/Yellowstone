package com.gpetuhov.android.yellowstone;

import android.support.v4.app.Fragment;


// Activity for details of earthquake
public class QuakeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        // Create fragment with details of earthquakes
        return new QuakeFragment();
    }

}
