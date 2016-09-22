package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;


// AsyncTaskLoader that fetches data from Flickr
public class PhotoLoader extends AsyncTaskLoader<List<PhotoListItem>> {

    public PhotoLoader(Context context) {
        super(context);
    }

    // Method is called, when LoaderManager's initLoader method is called
    @Override
    protected void onStartLoading() {
        // Triggers loadInBackground method to execute
        forceLoad();
    }

    // Background thread for fetching list of photos from Flickr
    @Override
    public List<PhotoListItem> loadInBackground() {
        // Create new QuakeFetcher object and return result of its fetchQuakes method
        return new PhotoFetcher().fetchPhotos();
    }
}

