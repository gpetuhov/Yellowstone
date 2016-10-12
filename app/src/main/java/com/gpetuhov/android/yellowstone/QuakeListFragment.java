package com.gpetuhov.android.yellowstone;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpetuhov.android.yellowstone.data.QuakeCursorWrapper;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;

// Fragment contains list of earthquakes.
// This fragment implements LoaderManager callbacks to update UI with data from loader.
// Host of this fragment must implement its Callbacks interface
// and set itself as a listener for the callbacks.
public class QuakeListFragment extends Fragment {

    // Quake internet loader ID
    public static final int QUAKE_NET_LOADER_ID = 1;

    // Quake database loader ID
    public static final int QUAKE_DB_LOADER_ID = 3;

    // RecyclerView for the list of earthquakes
    private RecyclerView mQuakeRecyclerView;

    // Adapter for the RecyclerView
    private QuakeAdapter mQuakeAdapter;

    // Empty view text (displayed when there is no data for RecyclerView)
    private TextView mEmptyView;

    // Listener to LoaderManager callbacks for quake net loader
    private QuakeNetLoaderListener mQuakeNetLoaderListener;

    // Listener to LoaderManager callbacks for quake database loader
    private QuakeDbLoaderListener mQuakeDbLoaderListener;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start or stop quake notifications service
        QuakePollService.setServiceAlarm(getActivity());

        // Create new quake net loader listener
        mQuakeNetLoaderListener = new QuakeNetLoaderListener();

        // Create new quake database loader listener
        mQuakeDbLoaderListener = new QuakeDbLoaderListener();
    }

    // Best practice to initialize a loader is in the fragment's onActivityCreated method,
    // but we do it in onResume, because we must reload data every time the fragment becomes visible
    // (user may return from Settings after changing query parameters).
    @Override
    public void onResume() {
        super.onResume();

        // Get reference to the LoaderManager
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();

        // Start new quake database loader or restart existing (start loading data from the database)
        // and set a listener for loader callbacks.
        loaderManager.initLoader(QUAKE_DB_LOADER_ID, null, mQuakeDbLoaderListener);

        // If there is a network connection
        if (QuakeUtils.isNetworkAvailableAndConnected(getActivity())) {
            // Start new quake net loader or restart existing (start loading data from the internet)
            // and set a listener for loader callbacks.
            loaderManager.restartLoader(QUAKE_NET_LOADER_ID, null, mQuakeNetLoaderListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create new adapter for the RecyclerView
        mQuakeAdapter = new QuakeAdapter();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quake_list, container, false);

        // Get access to RecyclerView
        mQuakeRecyclerView = (RecyclerView) v.findViewById(R.id.quake_recycler_view);

        // Set LinearLayoutManager for our RecyclerView (we need vertical scroll list)
        mQuakeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Attach adapter to the RecyclerView
        mQuakeRecyclerView.setAdapter(mQuakeAdapter);

        // Get access to TextView for empty view
        mEmptyView = (TextView) v.findViewById(R.id.empty_view);

        // If there is a network connection, display RecyclerView with data from USGS server
        if (QuakeUtils.isNetworkAvailableAndConnected(getActivity())) {
            // Display RecyclerView
            mQuakeRecyclerView.setVisibility(View.VISIBLE);

            // Hide empty view
            mEmptyView.setVisibility(View.GONE);

        } else {
            // Otherwise, display error

            // Hide RecyclerView
            mQuakeRecyclerView.setVisibility(View.GONE);

            // Display empty view
            mEmptyView.setVisibility(View.VISIBLE);
        }

        return v;
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


    // Adapter for our RecyclerView with list of earthquakes.
    // RecyclerView doesn't have CursorAdapter, so we have to
    // implement elements of CursorAdapter in our QuakeAdapter ourselves.
    private class QuakeAdapter extends RecyclerView.Adapter<QuakeHolder> {

        // Keeps cursor with the quakes table
        private QuakeCursorWrapper mCursor;

        public QuakeAdapter() {
        }

        // Method is called, when new view holder must be created
        @Override
        public QuakeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Get LayoutInflater from parent activity
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            // Create view for one list item from item layout
            View view = layoutInflater.inflate(R.layout.list_item_quake, parent, false);

            // Create ViewHolder with inflated view for one list item
            return new QuakeHolder(view);
        }

        // Method is called, when the view holder must be connected with data
        @Override
        public void onBindViewHolder(QuakeHolder holder, int position) {
            // Move cursor to the passed in position
            mCursor.moveToPosition(position);

            // Get earthquake at "position" from the cursor
            Quake quake = mCursor.getQuake();

            // Set ViewHolder of list item according to earthquake at "position"
            holder.bindQuake(quake);
        }

        // Return number of elements in RecyclerView
        @Override
        public int getItemCount() {
            // If the cursor is null, then number of items is 0
            if (mCursor == null) {
                return 0;
            }

            // Otherwise return number of rows in the cursor
            return mCursor.getCount();
        }

        // Swap cursor with new one
        public void swapCursor(Cursor newCursor) {
            // Create new quake cursor wrapper upon the passed in cursor
            mCursor = new QuakeCursorWrapper(newCursor);

            // Notify RecyclerView, that data has changed
            notifyDataSetChanged();
        }
    }


    // Listens to LoaderManager callbacks for quake net loader
    private class QuakeNetLoaderListener implements LoaderManager.LoaderCallbacks<Void> {

        // Returns new quake net loader
        @Override
        public Loader<Void> onCreateLoader(int id, Bundle args) {
            return new QuakeNetLoader(getActivity());
        }

        // Method is called, when load is finished
        @Override
        public void onLoadFinished(Loader<Void> loader, Void data) {

        }

        // Method is called when data from loader is no longer valid
        @Override
        public void onLoaderReset(Loader<Void> loader) {

        }
    }


    // Listens to LoaderManager callbacks for quake database loader
    private class QuakeDbLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        // Returns new quake database loader
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Create and return new cursor loader that loads quakes from quake table in the database
            return new CursorLoader(getActivity(),
                    QuakeEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }

        // Method is called, when load is finished
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap cursor for the RecyclerView adapter
            mQuakeAdapter.swapCursor(data);
        }

        // Method is called when data from loader is no longer valid
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Swap for the RecyclerView adapter with null value
            // (to release previously used cursor)
            mQuakeAdapter.swapCursor(null);
        }
    }

}
