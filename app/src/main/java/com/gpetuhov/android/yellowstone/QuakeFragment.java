package com.gpetuhov.android.yellowstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


// Fragment displays details of earthquake
public class QuakeFragment extends Fragment {

    // Key for fragment's argument with ID of the earthquake to display
    public static final String ARG_QUAKE_ID = "quake_id";

    // Stores details of earthquake to display
    private Quake mQuake;

    // TextView for earthquake location
    private TextView mQuakeLocationTextView;

    // TextView for earthquake magnitude
    private TextView mQuakeMagnitudeTextView;

    // TextView for earthquake date
    private TextView mQuakeDateTextView;

    // TextView for earthquake depth
    private TextView mQuakeDepthTextView;

    // TextView for earthquake coordinates
    private TextView mQuakeCoordinatesTextView;

    // Reference to Google Map
    private GoogleMap mGoogleMap;

    // Reference to MapView that displays Google Map.
    // We must use MapView instead of MapFragment, because here map occupies only part of the screen.
    // For proper functioning of MapView we must forward callback, that manage its lifecycle
    // (onCreate(), onResume() etc.)
    private MapView mMapView;

    // Return new instance of this fragment and attach arguments to it
    public static QuakeFragment newInstance(long quakeDbId) {

        // Create new empty Bundle object for fragment arguments
        Bundle args = new Bundle();

        // Put earthquake ID into Bundle object
        args.putLong(ARG_QUAKE_ID, quakeDbId);

        // Create new instance of this fragment
        QuakeFragment fragment = new QuakeFragment();

        // Attach arguments to fragment
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get earthquake ID from the fragment's arguments
        long quakeDbId = getArguments().getLong(ARG_QUAKE_ID);

        // Get earthquake with received ID from QuakeLab
        // and store it mQuake field
        mQuake = QuakeLab.get(getActivity()).getQuake(quakeDbId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quake, container, false);

        // Get access to TextView for earthquake location
        mQuakeLocationTextView = (TextView) v.findViewById(R.id.quake_location_text_view);

        // Get access to TextView for earthquake magnitude
        mQuakeMagnitudeTextView = (TextView) v.findViewById(R.id.quake_magnitude_text_view);

        // Get access to TextView for earthquake date
        mQuakeDateTextView = (TextView) v.findViewById(R.id.quake_date_text_view);

        // Get access to TextView for earthquake depth
        mQuakeDepthTextView = (TextView) v.findViewById(R.id.quake_depth_text_view);

        // Get access to TextView for earthquake coordinates
        mQuakeCoordinatesTextView = (TextView) v.findViewById(R.id.quake_coordinates_text_view);

        // Display earthquake location
        mQuakeLocationTextView.setText(mQuake.getLocation());

        // Display earthquake magnitude
        mQuakeMagnitudeTextView.setText("Magnitude " + mQuake.getFormattedMagnitude());

        // Display earthquake date
        mQuakeDateTextView.setText(mQuake.getFormattedDate());

        // Display earthquake depth
        mQuakeDepthTextView.setText("Depth " + mQuake.getFormattedDepth() + " km");

        // Display earthquake coordinates
        mQuakeCoordinatesTextView.setText(mQuake.getFormattedLatitude() + ", " + mQuake.getFormattedLongitude());


        // Get access to MapView for displaying map with the earthquake
        mMapView = (MapView) v.findViewById(R.id.quake_detail_mapview);

        // Callback must be forwarded for prover MapView lifecycle
        mMapView.onCreate(savedInstanceState);

        // Get access to Google Map displayed in MapView
        // Reference to Google Map is returned asynchronously, when the map is ready,
        // and is passed to OnMapReadyCallback listener,
        // in which we override onMapReady method to save returned reference to the map.
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // When the map is ready, return reference to it
                mGoogleMap = googleMap;

                // Enable zoom buttons
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

                // When the map is loaded, update it with new camera position and the marker of the earthquake.
                // To do this, we must set OnMapLoadedCallback listener for the map
                // and override its onMapLoaded method.
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        QuakeUtils.updateMap(getActivity(), mGoogleMap, mQuake);
                    }
                });
            }
        });

        return v;
    }

    // Callback must be forwarded for prover MapView lifecycle
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    // Callback must be forwarded for prover MapView lifecycle
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    // Callback must be forwarded for prover MapView lifecycle
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    // Callback must be forwarded for prover MapView lifecycle
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    // Callback must be forwarded for prover MapView lifecycle
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
