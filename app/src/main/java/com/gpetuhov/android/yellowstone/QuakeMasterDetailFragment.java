package com.gpetuhov.android.yellowstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


// Master/Detail fragment for list of quakes and details
// Contains nested fragments:
// On phone: nested fragment with list of quakes
// On tablet: nested fragment with list of quakes and nested fragment with details
// This fragment is the host for QuakeListFragment, so it must implement its Callbacks interface,
// and it must register itself as a listener to quake list item clicks in QuakeListFragment.
public class QuakeMasterDetailFragment extends Fragment
        implements QuakeListFragment.Callbacks {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // Layout is taken from alias in values/refs.xml file
        // For phone alias gives phone layout id
        // For tablets alias gives tablet layout id
        View v = inflater.inflate(R.layout.fragment_master_quake, container, false);

        // Get reference to the FragmentManager that manages child fragments
        FragmentManager fm = getChildFragmentManager();

        // Get reference to the child fragment with list of quakes from the fragment manager
        QuakeListFragment fragmentQuakeList =
                (QuakeListFragment) fm.findFragmentById(R.id.fragment_quake_list_container);

        // If this child fragment does not exist, create it and add to master/detail fragment
        if (fragmentQuakeList == null) {

            // Create new fragment with list of quakes
            fragmentQuakeList = new QuakeListFragment();

            // Set master/detail fragment as a listener to new fragment's callbacks
            fragmentQuakeList.setOnQuakeSelectedListener(this);

            // Add new fragment as a child to master/detail fragment
            fm.beginTransaction()
                    .add(R.id.fragment_quake_list_container, fragmentQuakeList)
                    .commit();

        } else {
            // Otherwise (fragments are recreated by Android after orientation change)
            // do not create new child fragment (because it is already recreated by Android)
            // and only set master/detail fragment as a listener to child's callbacks
            // (this must be done, because although fragments are recreated by Android,
            // they are not the same instances, that existed before orientation change,
            // so the new instance of master/detail fragment is set as a listener
            // to callbacks of the new instance of child's fragment)
            fragmentQuakeList.setOnQuakeSelectedListener(this);
        }

        return v;
    }

    // Method is called, when an item of RecyclerView in QuakeListFragment is clicked.
    // Depending on the device (phone or tablet),
    // we must either start new activity with quake details (on phone),
    // or display new child fragment with quake details in the corresponding FrameLayout.
    @Override
    public void onQuakeSelected(Quake quake) {

        // Check if the container (FrameLayout) for the child fragment with quake details exists
        // (on phone XML layout there is no such resource id, on tablet XML it is present).
        // This is recommended practice of detecting on which device we are working.
        if (getView().findViewById(R.id.fragment_quake_detail_container) == null) {
            // If resource id does not exist, then we are on phone and must start new activity

            // Create explicit intent to start activity with details of the earthquake
            Intent intent = QuakePagerActivity.newIntent(getActivity(), quake.getDbId());

            // Start activity with details of the earthquake
            startActivity(intent);

        } else {
            // If resource id exists, then we are on tablet and must create new child fragment and add it

            // Create new fragment with quake details and pass quake ID as the fragment argument
            Fragment newDetail = QuakeFragment.newInstance(quake.getDbId());

            // Get reference to fragment manager and replace fragment in the corresponding container
            // by the new fragment (this is done, because fragment container may already contain
            // previously selected earthquake).
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_quake_detail_container, newDetail)
                    .commit();
        }
    }

}
