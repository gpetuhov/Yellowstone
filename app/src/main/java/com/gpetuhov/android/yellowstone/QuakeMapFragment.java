package com.gpetuhov.android.yellowstone;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.gpetuhov.android.yellowstone.data.QuakeCursorLoaderFactory;
import com.gpetuhov.android.yellowstone.utils.UtilsMap;

import java.util.List;

import javax.inject.Inject;


// Fragment contains map with earthquakes.
// Here we use SupportMapFragment, because the map takes up the whole screen.
// For SupportMapFragment we don't have to forward lifecycle callbacks.
public class QuakeMapFragment extends SupportMapFragment {

    // This fragment's CursorLoader ID
    private static final int QUAKE_MAP_LOADER_ID = 2;

    // Keeps instance of UtilsQuakeList. Injected by Dagger.
    @Inject UtilsMap mUtilsMap;

    // Keeps instance of QuakeCursorLoaderFactory. Injected by Dagger.
    @Inject QuakeCursorLoaderFactory mQuakeCursorLoaderFactory;

    // Reference to Google Map
    private GoogleMap mGoogleMap;

    // Keeps list of quakes
    private List<Quake> mQuakes;

    // Listener to LoaderManager callbacks for quake list loader
    private QuakeMapCursorLoaderListener mQuakeMapCursorLoaderListener;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Inject UtilsQuakeList into this fragment
        YellowstoneApp.getAppComponent().inject(this);

        mQuakeMapCursorLoaderListener = new QuakeMapCursorLoaderListener();

        // Asynchronously get reference to the map
        getMapAsync(googleMap -> {
            // When the map is ready, get reference to it
            mGoogleMap = googleMap;

            // Enable zoom buttons
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

            // When the map is loaded, update it with all earthquakes from quake table.
            // To do this, we must set OnMapLoadedCallback listener for the map
            // and override its onMapLoaded method.
            mGoogleMap.setOnMapLoadedCallback(() -> mUtilsMap.updateMap(mGoogleMap, mQuakes));
        });
    }

    // Best practice to initialize a loader is in the fragment's onActivityCreated method,
    // but we do it in onResume, because we must reload data every time the fragment becomes visible
    // (user may return from Settings after changing query parameters).
    @Override
    public void onResume() {
        super.onResume();

        // Get reference to the LoaderManager
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();

        // Initialize quake list loader and set a listener for loader callbacks.
        // restartLoader always reloads data.
        loaderManager.restartLoader(QUAKE_MAP_LOADER_ID, null, mQuakeMapCursorLoaderListener);
    }

    // Updates quake list field
    private void updateQuakeList(Cursor data) {
        // Get list of quakes from the cursor and save it to the field
        mQuakes = Quake.getQuakeListFromCursor(data);
    }

    // Listens to LoaderManager callbacks for quake list loader
    private class QuakeMapCursorLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        // Returns new quake list loader
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Create and return new cursor loader that loads quakes from quake table
            return mQuakeCursorLoaderFactory.createQuakeCursorLoader();
        }

        // Method is called, when load is finished
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Update quake list field with new cursor
            updateQuakeList(data);

            // Update map with new list of quakes
            mUtilsMap.updateMap(mGoogleMap, mQuakes);
        }

        // Method is called when data from loader is no longer valid
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Do nothing, keep map with previously loaded list of quakes
        }
    }
}
