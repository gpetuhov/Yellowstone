package com.gpetuhov.android.yellowstone;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// Fragment contains list of earthquakes
public class QuakeListFragment extends Fragment {

    // RecyclerView for the list of earthquakes
    private RecyclerView mQuakeRecyclerView;

    // Empty view text (displayed when there is no data for RecyclerView)
    private TextView mEmptyView;

    // Empty list for the list of earthquakes
    private List<Quake> mQuakes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fragment must not be destroyed when device is rotated
        // Because we use AsyncTask to fetch data
        setRetainInstance(true);

        // If there is a network connection, fetch data
        if (isNetworkAvailableAndConnected()) {
            // Fetch list of earthquakes from USGS server in background thread
            new FetchQuakesTask().execute();
        }

    }

    // Return "true" if network is available and connected
    private boolean isNetworkAvailableAndConnected() {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // True if network is available and connected
        boolean isNetworkConnected = networkInfo != null && networkInfo.isConnected();

        return isNetworkConnected;
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
        if (isNetworkAvailableAndConnected()) {
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

        // If the fragment is added to a parent activity,
        // create new adapter with list of quakes stored in mQuakes
        // and set it as adapter for the RecyclerView
        if (isAdded()) {
            mQuakeRecyclerView.setAdapter(new QuakeAdapter(mQuakes));
        }
    }


    // === Inner classes =====================

    // ViewHolder for our RecyclerView with list of earthquakes
    // Our ViewHolder implements View.OnClickListener to handle clicks on list items
    private class QuakeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

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
            // Get magnitude from the Quake object and display it in TextView
            mMagnitudeTextView.setText(quake.getFormattedMagnitude());

            // Get location from the Quake object and display it in TextView
            mLocationTextView.setText(quake.getLocation());

            // Get date from the Quake object and display it in TextView
            mDateTextView.setText(quake.getFormattedDate());
        }

        // Handle clicks on list items
        @Override
        public void onClick(View v) {

            // Create explicit intent to start activity with details of the earthquake
            Intent intent = new Intent(getActivity(), QuakeActivity.class);

            // Start activity with details of the earthquake
            startActivity(intent);
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


    // Background thread for fetching list of earthquakes from request URL
    private class FetchQuakesTask extends AsyncTask<Void, Void, List<Quake>> {

        @Override
        protected List<Quake> doInBackground(Void... params) {

            // Create new QuakeFetcher object and return result of its fetchQuakes method
            return new QuakeFetcher().fetchQuakes();
        }

        @Override
        protected void onPostExecute(List<Quake> quakes) {

            // Store list of quakes fetched from USGS server in mQuakes field of QuakeListFragment
            mQuakes = quakes;

            // Create new adapter for RecyclerView with new list of quakes
            setupAdapter();
        }
    }

}
