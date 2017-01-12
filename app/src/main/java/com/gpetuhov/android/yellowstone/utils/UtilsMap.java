package com.gpetuhov.android.yellowstone.utils;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpetuhov.android.yellowstone.Quake;
import com.gpetuhov.android.yellowstone.R;

import java.util.List;

// Map utilities
public class UtilsMap {

    // Caldera location on map
    public static final String CALDERA_LATITUDE = "44.5";       // Latitude of the center of caldera (degrees)
    public static final String CALDERA_LONGITUDE = "-110.6";    // Longitude of the center of caldera (degrees)
    public static final String CALDERA_RADIUS = "40";           // Radius of caldera (in kilometers)

    // Shifts from Caldera to include on map
    private static final double LAT_SHIFT = 0.5;
    private static final double LNG_SHIFT = 0.6;

    private Context mContext;

    public UtilsMap(Context context) {
        mContext = context;
    }

    // Updates map with bounds including Caldera and points earthquakes,
    // displays list of quakes on map.
    public void updateMap(GoogleMap googleMap, List<Quake> quakes) {

        // Do nothing, if map is not ready
        if (null == googleMap) {
            return;
        }

        // Do nothing, if list is null
        if (null == quakes) {
            return;
        }

        // Coordinates of Caldera
        double calderaLat = getCalderaLatDouble();
        double calderaLng = getCalderaLngDouble();

        // North shift from Caldera
        LatLng northOfCaldera = new LatLng(calderaLat + LAT_SHIFT, calderaLng);

        // South shift from Caldera
        LatLng southOfCaldera = new LatLng(calderaLat - LAT_SHIFT, calderaLng);

        // West shift from Caldera
        LatLng westOfCaldera = new LatLng(calderaLat, calderaLng - LNG_SHIFT);

        // East shift from Caldera
        LatLng eastOfCaldera = new LatLng(calderaLat, calderaLng + LNG_SHIFT);

        // Clear map
        googleMap.clear();

        // New latitude longitude bounds builder
        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

        // Include shifts from Caldera to bounds builder
        latLngBoundsBuilder
                .include(northOfCaldera)
                .include(southOfCaldera)
                .include(westOfCaldera)
                .include(eastOfCaldera);

        // For each earthquake from the list
        for (Quake quakeToDisplay : quakes) {

            // Create point of the earthquake
            LatLng pointOfQuake = new LatLng(quakeToDisplay.getLatitude(), quakeToDisplay.getLongitude());

            // Build marker for the point of the earthquake
            MarkerOptions quakeMarker = new MarkerOptions().position(pointOfQuake);

            // Add marker for the earthquake to map
            googleMap.addMarker(quakeMarker);

            latLngBoundsBuilder.include(pointOfQuake);
        }

        // Build bounds from bounds builder
        LatLngBounds bounds = latLngBoundsBuilder.build();

        // Get map margin size from XML
        int margin = mContext.getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

        // Build camera update with the bounds built above and map margins
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, margin);

        // Move camera to the update built above
        googleMap.moveCamera(cameraUpdate);
    }

    // Return Caldera latitude converted to double
    private static double getCalderaLatDouble() {
        return Double.parseDouble(CALDERA_LATITUDE);
    }

    // Return Caldera longitude converted to double
    private static double getCalderaLngDouble() {
        return Double.parseDouble(CALDERA_LONGITUDE);
    }
}
