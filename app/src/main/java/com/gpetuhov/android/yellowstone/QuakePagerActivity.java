package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;

import com.gpetuhov.android.yellowstone.data.QuakeCursorLoaderFactory;

import java.util.List;

import javax.inject.Inject;


// Activity for details of the earthquake.
// Uses ViewPager to swipe between earthquakes.
public class QuakePagerActivity extends VisibleActivity {

    // This activity's CursorLoader ID
    private static final int QUAKE_PAGER_LOADER_ID = 4;

    // Key for extra data in intent
    private static final String EXTRA_KEY_QUAKE = "quake_extra_key";

    // Keeps instance of QuakeCursorLoaderFactory. Injected by Dagger.
    @Inject QuakeCursorLoaderFactory mQuakeCursorLoaderFactory;

    // Stores view pager to swipe between earthquakes
    private ViewPager mViewPager;

    // List of earthquakes
    private List<Quake> mQuakes;

    // Listener to LoaderManager callbacks for quake list loader
    private QuakePagerCursorLoaderListener mQuakePagerCursorLoaderListener;

    // Return new intent with extra data to start this activity
    public static Intent newIntent(Context packageContext, Quake quake) {

        // Create explicit intent to start this activity
        Intent intent = new Intent(packageContext, QuakePagerActivity.class);

        // Put ID of earthquake as extra data in intent
        intent.putExtra(EXTRA_KEY_QUAKE, quake);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject UtilsQuakeList into this activity
        YellowstoneApp.getAppComponent().inject(this);

        mQuakePagerCursorLoaderListener = new QuakePagerCursorLoaderListener();

        // Set layout for the activity
        setContentView(R.layout.activtiy_quake_pager);

        // Get reference to view pager
        mViewPager = (ViewPager) findViewById(R.id.activity_quake_pager_viewpager);

        // Get reference to the LoaderManager
        LoaderManager loaderManager = getSupportLoaderManager();

        // Initialize quake list loader and set a listener for loader callbacks
        loaderManager.initLoader(QUAKE_PAGER_LOADER_ID, null, mQuakePagerCursorLoaderListener);
    }

    // Updates ViewPager with new data
    private void updateViewPager() {
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
                // Quake to display is passed to the instance of QuakeFragment
                // as a fragment argument.
                return QuakeFragment.newInstance(quake);
            }

            @Override
            public int getCount() {
                // Return size of the list of quakes
                return mQuakes.size();
            }
        });
    }

    // Listens to LoaderManager callbacks for quake list loader
    private class QuakePagerCursorLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        // Returns new quake list loader
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Create and return new cursor loader that loads quakes from quake table
            return mQuakeCursorLoaderFactory.createQuakeCursorLoader();
        }

        // Method is called, when load is finished
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Get quake list from the loaded cursor
            mQuakes = Quake.getQuakeListFromCursor(data);

            // Update ViewPager with new quake list
            updateViewPager();

            // Get quake from extra data of the received intent, that started this activity
            Quake quake = (Quake) getIntent().getSerializableExtra(EXTRA_KEY_QUAKE);

            // Set position of this quake as the position of current item for the ViewPager.
            mViewPager.setCurrentItem(mQuakes.indexOf(quake));
        }

        // Method is called when data from loader is no longer valid
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Do nothing, keep ViewPager with previously loaded list of quakes
        }
    }
}
