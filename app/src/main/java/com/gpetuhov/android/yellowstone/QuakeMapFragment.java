package com.gpetuhov.android.yellowstone;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


// Fragment contains map with earthquakes.
// Here we use SupportMapFragment, because the map takes up the whole screen.
// For SupportMapFragment we don't have to forward lifecycle callbacks.
public class QuakeMapFragment extends SupportMapFragment {

    // Reference to Google Map
    private GoogleMap mGoogleMap;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Asynchronously get reference to the map
        getMapAsync(googleMap -> {
            // When the map is ready, get reference to it
            mGoogleMap = googleMap;

            // Enable zoom buttons
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

            // When the map is loaded, update it with all earthquakes from quake table.
            // To do this, we must set OnMapLoadedCallback listener for the map
            // and override its onMapLoaded method.
            mGoogleMap.setOnMapLoadedCallback(() -> QuakeUtils.updateMap(getActivity(), mGoogleMap, null));
        });
    }
}
