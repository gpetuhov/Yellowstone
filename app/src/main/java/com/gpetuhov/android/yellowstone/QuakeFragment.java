package com.gpetuhov.android.yellowstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


// Fragment displays details of earthquake
public class QuakeFragment extends Fragment {

    // Key for fragment's argument with ID of the earthquake to display
    public static final String ARG_QUAKE_ID = "quake_id";

    // Stores details of earthquake to display
    private Quake mQuake;

    // TextView for earthquake location
    private TextView mQuakeLocationTextView;

    // Return new instance of this fragment and attach arguments to it
    public static QuakeFragment newInstance(String quakeId) {

        // Create new empty Bundle object for fragment arguments
        Bundle args = new Bundle();

        // Put earthquake ID into Bundle object
        args.putString(ARG_QUAKE_ID, quakeId);

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
        String quakeID = getArguments().getString(ARG_QUAKE_ID);

        // Get earthquake with received ID from QuakeLab
        // and store it mQuake field
        mQuake = QuakeLab.get(getActivity()).getQuake(quakeID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quake, container, false);

        // Get access to TextView for earthquake location
        mQuakeLocationTextView = (TextView) v.findViewById(R.id.quake_location_text_view);

        // Display earthquake location
        mQuakeLocationTextView.setText(mQuake.getLocation());

        return v;
    }

}
