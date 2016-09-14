package com.gpetuhov.android.yellowstone;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


// Main activity with Tabs and ViewPager
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get access to ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create adapter for switching between fragments
        CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager());

        // Set the adapter for the ViewPager
        viewPager.setAdapter(adapter);

        // Get access to TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);
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

            // TO DO: Specify action for Settings menu item here

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
