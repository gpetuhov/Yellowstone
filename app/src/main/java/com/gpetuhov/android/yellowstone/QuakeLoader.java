package com.gpetuhov.android.yellowstone;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

// AsyncTaskLoader that fetches data from USGS server
public class QuakeLoader extends AsyncTaskLoader<List<Quake>> {

    private Context mContext;

    public QuakeLoader(Context context) {
        super(context);

        // Save application context in the field (it is used in loadInBackground method)
        mContext = context;
    }

    // Method is called, when LoaderManager's initLoader method is called
    @Override
    protected void onStartLoading() {
        // Triggers loadInBackground method to execute
        forceLoad();
    }

    // Background thread for fetching list of earthquakes from request URL
    @Override
    public List<Quake> loadInBackground() {
        // Create new QuakeFetcher object and return result of its fetchQuakes method
        // TODO: change to fetchQuakes after tests
        return new QuakeFetcher().fetchAllWorldQuakes(mContext);
    }
}
