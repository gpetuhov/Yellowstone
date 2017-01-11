package com.gpetuhov.android.yellowstone;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import javax.inject.Inject;


// AsyncTaskLoader that fetches data from Flickr
public class PhotoLoader extends AsyncTaskLoader<List<PhotoListItem>> {

    // Keeps instance of PhotoFetcher. Injected by Dagger.
    @Inject PhotoFetcher mPhotoFetcher;

    public PhotoLoader(Context context) {
        super(context);

        // Inject PhotoFetcher into PhotoLoader
        YellowstoneApp.getAppComponent().inject(this);
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
        return mPhotoFetcher.fetchPhotos();
    }
}

