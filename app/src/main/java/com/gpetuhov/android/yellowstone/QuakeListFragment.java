package com.gpetuhov.android.yellowstone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// Fragment contains list of earthquakes.
// This fragment implements LoaderManager callbacks to update UI with data from loader.
// Host of this fragment must implement its Callbacks interface
// and set itself as a listener for the callbacks.
public class QuakeListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Quake>> {

    // Loader ID
    public static final int QUAKE_LOADER_ID = 1;

    // RecyclerView for the list of earthquakes
    private RecyclerView mQuakeRecyclerView;

    // Empty view text (displayed when there is no data for RecyclerView)
    private TextView mEmptyView;

    // Stores reference to a host that uses this fragment
    // Host must implement Callbacks interface
    private Callbacks mCallbacks;


    // This interface must be implemented by the host (activity or parent fragment) that uses this fragment
    public interface Callbacks {
        // The host must override this method
        void onQuakeSelected(Quake quake);
    }

    // Host that uses this fragment must call this method and pass reference to itself
    // to be registered as a listener that implements Callbacks interface.
    public void setOnQuakeSelectedListener(Callbacks host) {
        // Save reference to the host
        mCallbacks = host;
    }

    // Best practice to initialize a loader is in the fragment's onActivityCreated method,
    // but we do it in onResume, because we must reload data every time the fragment becomes visible
    // (user may return from Settings after changing query parameters).
    @Override
    public void onResume() {
        super.onResume();

        // If there is a network connection
        if (QuakeUtils.isNetworkAvailableAndConnected(getActivity())) {

            // Get reference to the LoaderManager
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();

            // Start new loader or restart existing (start loading data)
            // and set this fragment as a listener for loader callbacks.
            loaderManager.restartLoader(QUAKE_LOADER_ID, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quake_list, container, false);

        // Get access to RecyclerView
        mQuakeRecyclerView = (RecyclerView) v.findViewById(R.id.quake_recycler_view);

        // Set LinearLayoutManager for our RecyclerView (we need vertical scroll list)
        mQuakeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get access to TextView for empty view
        mEmptyView = (TextView) v.findViewById(R.id.empty_view);

        // If there is a network connection, display RecyclerView with data from USGS server
        if (QuakeUtils.isNetworkAvailableAndConnected(getActivity())) {
            // Display RecyclerView
            mQuakeRecyclerView.setVisibility(View.VISIBLE);

            // Hide empty view
            mEmptyView.setVisibility(View.GONE);

            // Set adapter for RecyclerView
            setupAdapter();
        } else {
            // Otherwise, display error

            // Hide RecyclerView
            mQuakeRecyclerView.setVisibility(View.GONE);

            // Display empty view
            mEmptyView.setVisibility(View.VISIBLE);
        }

        return v;
    }

    // Set adapter for our RecyclerView
    private void setupAdapter() {

        // If the fragment is added to a parent activity
        if (isAdded()) {

            // Get list of quakes from QuakeLab
            List<Quake> quakes = QuakeLab.get(getActivity()).getQuakes();

            // Create new adapter with this list of quakes
            // and set it as adapter for the RecyclerView
            mQuakeRecyclerView.setAdapter(new QuakeAdapter(quakes));
        }
    }

    // LoaderManager callbacks

    // Returns new loader
    @Override
    public Loader<List<Quake>> onCreateLoader(int id, Bundle args) {
        return new QuakeLoader(getActivity());
    }

    // When load is finished, saves the fetched data and updates UI
    @Override
    public void onLoadFinished(Loader<List<Quake>> loader, List<Quake> data) {
        // Replace list in QuakeLab with quakes fetched from USGS server
        QuakeLab.get(getActivity()).setQuakes(data);

        // Create new adapter for RecyclerView with new list of quakes
        setupAdapter();
    }

    // Method is called when data from loader is no longer valid
    @Override
    public void onLoaderReset(Loader<List<Quake>> loader) {
    }


    // === Inner classes =====================

    // ViewHolder for our RecyclerView with list of earthquakes
    // Our ViewHolder implements View.OnClickListener to handle clicks on list items
    private class QuakeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Stores earthquake for this ViewHolder
        private Quake mQuake;

        // TextView for the magnitude of the earthquake
        public TextView mMagnitudeTextView;

        // TextView for the location of the earthquake
        public TextView mLocationTextView;

        // TextView for the date of the earthquake
        public TextView mDateTextView;

        public QuakeHolder(View itemView) {
            super(itemView);

            // Our class handles clicks on list items by itself
            itemView.setOnClickListener(this);

            // Get access to TextViews in itemView
            mMagnitudeTextView = (TextView) itemView.findViewById(R.id.magnitude);
            mLocationTextView = (TextView) itemView.findViewById(R.id.location);
            mDateTextView = (TextView) itemView.findViewById(R.id.date);
        }

        public void bindQuake(Quake quake) {

            // Store quake object in private field
            mQuake = quake;

            // Get magnitude from the Quake object and display it in TextView
            mMagnitudeTextView.setText(mQuake.getFormattedMagnitude());

            // Get location from the Quake object and display it in TextView
            mLocationTextView.setText(mQuake.getLocation());

            // Get date from the Quake object and display it in TextView
            mDateTextView.setText(mQuake.getFormattedDate());
        }

        // Handle clicks on list items
        @Override
        public void onClick(View v) {
            // Forward callback to the host, that uses this fragment, by calling its onQuakeSelected method.
            // All real action is implemented by the host.
            mCallbacks.onQuakeSelected(mQuake);
        }
    }


    // Adapter for our RecyclerView with list of earthquakes
    private class QuakeAdapter extends RecyclerView.Adapter<QuakeHolder> {

        // Empty list for the list of earthquakes
        private List<Quake> mItems;

        public QuakeAdapter(List<Quake> items) {
            mItems = items;
        }

        @Override
        public QuakeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Get LayoutInflater from parent activity
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            // Create view for one list item from item layout
            View view = layoutInflater.inflate(R.layout.list_item_quake, parent, false);

            // Create ViewHolder with inflated view for one list item
            return new QuakeHolder(view);
        }

        @Override
        public void onBindViewHolder(QuakeHolder holder, int position) {
            // Get earthquake at "position" from list of quakes
            Quake quake = mItems.get(position);

            // Set ViewHolder of list item according to earthquake at "position"
            holder.bindQuake(quake);
        }

        @Override
        public int getItemCount() {
            // Return size of list of earthquakes
            return mItems.size();
        }
    }

}
