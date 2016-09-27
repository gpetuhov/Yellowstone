package com.gpetuhov.android.yellowstone;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


// Main activity with Tabs and ViewPager
public class MainActivity extends AppCompatActivity {

    // Last ViewPager position key in SharedPreferences
    public static final String PREF_LAST_PAGE = "lastViewPagerPosition";

    // Default ViewPager position
    public static final int DEFAULT_PAGE = 0;

    // ViewPager for displaying main pages of the app
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .edit()
                        .putInt(PREF_LAST_PAGE, position)
                        .apply();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Get access to TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(mViewPager);
    }


    // Update ViewPager position with last position stored in SharedPreferences
    // (if a user returns from another activity, ViewPager opens
    // last opened page instead of the starting page)
    @Override
    protected void onResume() {
        super.onResume();

        // Get last ViewPager position from SharedPreferences.
        // If null, return default page number (starting page).
        int position = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(PREF_LAST_PAGE, DEFAULT_PAGE);

        // Update ViewPager position with new value
        mViewPager.setCurrentItem(position);
    }


    // Create menu for main activity (each fragment can later add its items to this menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    // Specify action for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            // TODO: Specify action for Settings menu item here

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
