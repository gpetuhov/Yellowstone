package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.gpetuhov.android.yellowstone.sync.YellowstoneSyncAdapter;
import com.gpetuhov.android.yellowstone.utils.UtilsPrefs;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


// Main activity with Tabs and ViewPager
public class MainActivity extends VisibleActivity {

    // Last ViewPager position key in SharedPreferences
    public static final String PREF_LAST_PAGE = "lastViewPagerPosition";

    // Default ViewPager position
    public static final int DEFAULT_PAGE = 0;

    // Keeps instance of UtilsPrefs. Injected by Dagger.
    @Inject UtilsPrefs mUtilsPrefs;

    // ViewPager for displaying main pages of the app
    private ViewPager mViewPager;

    // Keeps subscription of RxJava Subscriber to Observable
    private Subscription mSubscription;

    // Return new intent to start this activity
    public static Intent newIntent(Context context) {
        // Create explicit intent to start this activity
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inject SharedPreference instance into this activity field
        YellowstoneApp.getAppComponent().inject(this);

        // Get reference to ViewPager
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create adapter for switching between fragments
        CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager());

        // Set the adapter for the ViewPager
        mViewPager.setAdapter(adapter);

        // Create and set new listener for ViewPager page changes
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Save current ViewPager position to SharedPreferences
                mUtilsPrefs.putIntToSharedPreferences(PREF_LAST_PAGE, position);

                // Make activity recreate menu
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Get access to TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect TabLayout with ViewPager
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        // If previously no quakes were fetched
        if (mUtilsPrefs.isPreviouslyFetchedQuakeNotExist()) {
            // Start fetching data from the network
            YellowstoneSyncAdapter.syncImmediately(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewPagerPositionWithSavedValue();
    }

    // Update ViewPager position with last position stored in SharedPreferences
    // (if a user returns from another activity, ViewPager opens
    // last opened page instead of the starting page).
    // Read from SharedPreferences in background thread.
    private void updateViewPagerPositionWithSavedValue() {
        // Unsubscribe previous subscription
        unsubscribeSubscription();

        // Subscribe Subscriber to Observable and save subscription
        mSubscription = Observable.create(new Observable.OnSubscribe<Integer>() {   // Create observable
            // Method is called, when subscriber subscribes
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                // Get last ViewPager position from SharedPreferences.
                // If null, return default page number (starting page).
                Integer position = mUtilsPrefs.getIntFromSharedPreferences(PREF_LAST_PAGE, DEFAULT_PAGE);

                // Emit position to subscriber
                subscriber.onNext(position);

                // Observable finish emitting
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())   // Observable executes in background thread
                .observeOn(AndroidSchedulers.mainThread())  // Subscriber executes in main thread
                .subscribe(new Subscriber<Integer>() {  // Create subscriber
                    @Override
                    public void onCompleted() {
                        // Do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Do nothing
                    }

                    @Override
                    public void onNext(Integer position) {
                        // Update ViewPager position with new value
                        mViewPager.setCurrentItem(position);

                        // Make activity recreate menu
                        invalidateOptionsMenu();
                    }
                });
    }

    // Unsubscribe subscription
    private void unsubscribeSubscription() {
        if (null != mSubscription) {
            if (!mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
        }
    }

    // Create menu for main activity (each fragment can later add its items to this menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // If current view pager position is quake list
        if (mViewPager.getCurrentItem() == 0) {
            // Enable refresh button
            menu.findItem(R.id.action_refresh).setVisible(true);

        } else {
            // Otherwise disable refresh button
            menu.findItem(R.id.action_refresh).setVisible(false);
        }

        return true;
    }


    // Specify action for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Get selected item ID
        int id = item.getItemId();

        // If user selected Settings item
        if (id == R.id.action_settings) {

            // Create explicit intent to start settings activity
            Intent intent = new Intent(this, SettingsActivity.class);

            // Start settings activity
            startActivity(intent);

            return true;
        }

        // If user selected refresh button
        if (id == R.id.action_refresh) {

            // Fetch data from the network
            YellowstoneSyncAdapter.syncImmediately(this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unsubscribe subscription to prevent memory leaks
        unsubscribeSubscription();
    }
}
