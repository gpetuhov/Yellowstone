package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.gpetuhov.android.yellowstone.utils.UtilsQuakeList;

import java.util.List;

import javax.inject.Inject;


// Activity for details of the earthquake.
// Uses ViewPager to swipe between earthquakes.
public class QuakePagerActivity extends VisibleActivity {

    // Key for extra data in intent
    private static final String EXTRA_QUAKE_ID = "quake_id_extra_data";

    // Keeps instance of UtilsQuakeList. Injected by Dagger.
    @Inject UtilsQuakeList mUtilsQuakeList;

    // Stores view pager to swipe between earthquakes
    private ViewPager mViewPager;

    // List of earthquakes
    private List<Quake> mQuakes;

    // Return new intent with extra data to start this activity
    public static Intent newIntent(Context packageContext, long quakeDbId) {

        // Create explicit intent to start this activity
        Intent intent = new Intent(packageContext, QuakePagerActivity.class);

        // Put ID of earthquake as extra data in intent
        intent.putExtra(EXTRA_QUAKE_ID, quakeDbId);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject UtilsQuakeList into this activity
        YellowstoneApp.getAppComponent().inject(this);

        // Set layout for the activity
        setContentView(R.layout.activtiy_quake_pager);

        // Get reference to view pager
        mViewPager = (ViewPager) findViewById(R.id.activity_quake_pager_viewpager);

        // Get list of quakes
        mQuakes = mUtilsQuakeList.getQuakes();

        // Get fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create new adapter and set it for the ViewPager
        // FragmentStatePagerAdapter loads in advance only nearest fragments to the current one
        // (total amount of loaded fragments is not more than 3),
        // so it is good for displaying long lists.
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                // Get quake at "position" from the list
                Quake quake = mQuakes.get(position);

                // Return QuakeFragment with details of the quake at "position".
                // ID of the quake to display is passed to the instance of QuakeFragment
                // as a fragment argument.
                return QuakeFragment.newInstance(quake.getDbId());
            }

            @Override
            public int getCount() {
                // Return size of the list of quakes
                return mQuakes.size();
            }
        });

        // Get earthquake ID from extra data of the received intent, that started this activity
        long quakeDbId = getIntent().getLongExtra(EXTRA_QUAKE_ID, 0);

        // Look through the list of quakes and find the quake with ID matching the ID from the intent.
        // Set position of this quake as the position of current item for the ViewPager.
        for (int i = 0; i < mQuakes.size(); i++) {
            if (mQuakes.get(i).getDbId() == quakeDbId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
