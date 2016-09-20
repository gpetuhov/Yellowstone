package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


// Adapter for switching between fragments in ViewPager
public class CategoryAdapter extends FragmentPagerAdapter {

    // Context of the app
    private Context mContext;

    // Constructor saves context of the app
    // It will be used to get access to string resources
    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // Return proper fragment according to the opened page
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new QuakeListFragment();
        } else if (position == 1) {
            return new QuakeMapFragment();
        } else {
            return new PhotoListFragment();
        }
    }

    // Return number of pages
    @Override
    public int getCount() {
        return 3;
    }

    // Return page titles
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.category_quakes);
        } else if (position == 1) {
            return mContext.getString(R.string.category_map);
        } else {
            return mContext.getString(R.string.category_photos);
        }
    }
}
