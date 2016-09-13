package com.gpetuhov.android.yellowstone;


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

    private RecyclerView mQuakeRecyclerView;    // RecyclerView for list of quakes
    private QuakeAdapter mAdapter;              // Adapter for RecyclerView

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

        updateUI();

        return v;
    }

    // Set adapter for our RecyclerView
    private void updateUI() {
        mAdapter = new QuakeAdapter();
        mQuakeRecyclerView.setAdapter(mAdapter);
    }


    // === Inner classes =====================

    // ViewHolder for our RecyclerView with list of earthquakes
    private class QuakeHolder extends RecyclerView.ViewHolder {

        public TextView mTitleTextView; // Text for one list row

        public QuakeHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView; // In our test code itemView is one line of text. This will change later
        }
    }


    // Adapter for our RecyclerView with list of earthquakes
    private class QuakeAdapter extends RecyclerView.Adapter<QuakeHolder> {

        private List<String> mQuakes;   // Stores list of earthquakes

        // In constructor we create simple list of strings for test purpose
        public QuakeAdapter() {
            mQuakes = new ArrayList<>();

            for (int i = 0; i < 100; i++) {
                mQuakes.add("Quake " + i);
            }
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
            String quake = mQuakes.get(position);

            // Set ViewHolder of list item according to earthquake at "position"
            holder.mTitleTextView.setText(quake);
        }

        @Override
        public int getItemCount() {
            // Return size of list of earthquakes
            return mQuakes.size();
        }
    }

}
