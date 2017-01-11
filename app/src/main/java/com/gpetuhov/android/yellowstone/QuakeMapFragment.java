package com.gpetuhov.android.yellowstone;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.gpetuhov.android.yellowstone.utils.UtilsMap;

import javax.inject.Inject;


// Fragment contains map with earthquakes.
// Here we use SupportMapFragment, because the map takes up the whole screen.
// For SupportMapFragment we don't have to forward lifecycle callbacks.
public class QuakeMapFragment extends SupportMapFragment {

    // Keeps instance of UtilsQuakeList. Injected by Dagger.
    @Inject UtilsMap mUtilsMap;

    // Reference to Google Map
    private GoogleMap mGoogleMap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Inject UtilsQuakeList into this fragment
        YellowstoneApp.getAppComponent().inject(this);

        // Asynchronously get reference to the map
        getMapAsync(googleMap -> {
            // When the map is ready, get reference to it
            mGoogleMap = googleMap;

            // Enable zoom buttons
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

            // When the map is loaded, update it with all earthquakes from quake table.
            // To do this, we must set OnMapLoadedCallback listener for the map
            // and override its onMapLoaded method.
            mGoogleMap.setOnMapLoadedCallback(() -> mUtilsMap.updateMap(mGoogleMap, null));
        });
    }
}
