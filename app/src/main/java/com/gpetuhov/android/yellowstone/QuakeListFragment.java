package com.gpetuhov.android.yellowstone;


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

    // Empty list for the list of earthquakes
    private List<Quake> mQuakes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);    // Fragment is not destroyed when device is rotated (need to be removed later)

        // Fetch list of earthquakes from USGS server in background thread
        new FetchQuakesTask().execute();
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

        // Set adapter for our RecyclerView
        setupAdapter();

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
    private class QuakeHolder extends RecyclerView.ViewHolder {

        // TextView for the location of the earthquake
        public TextView mLocationTextView;

        public QuakeHolder(View itemView) {
            super(itemView);
            mLocationTextView = (TextView) itemView; // In our test code itemView is one line of text. This will change later
        }

        public void bindQuake(Quake quake) {
            // Get location from the Quake object and display it in TextView
            mLocationTextView.setText(quake.getLocation());
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
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);

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
