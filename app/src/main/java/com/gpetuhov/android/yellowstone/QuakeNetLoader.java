package com.gpetuhov.android.yellowstone;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

// AsyncTaskLoader that fetches data from USGS server
public class QuakeNetLoader extends AsyncTaskLoader<Void> {

    private Context mContext;

    public QuakeNetLoader(Context context) {
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
    public Void loadInBackground() {
        // Create new QuakeFetcher object and fetch data
        new QuakeFetcher().fetchQuakes(mContext);
        return null;
    }
}
