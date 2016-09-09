package com.gpetuhov.android.yellowstone;

import android.support.v4.app.Fragment;


// Activity for list of earthquakes
public class QuakeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new QuakeListFragment(); // Create fragment with list of earthquakes
    }

}
