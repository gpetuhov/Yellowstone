package com.gpetuhov.android.yellowstone;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpetuhov.android.yellowstone.data.QuakeCursorLoaderFactory;
import com.gpetuhov.android.yellowstone.utils.UtilsNet;
import com.gpetuhov.android.yellowstone.utils.UtilsPrefs;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// Fragment contains list of earthquakes.
// This fragment implements LoaderManager callbacks to update UI with data from loader.
// Host of this fragment must implement its Callbacks interface
// and set itself as a listener for the callbacks.
public class QuakeListFragment extends Fragment {

    // Quake list loader ID
    public static final int QUAKE_LIST_LOADER_ID = 1;

    // Keeps instance of UtilsPrefs. Injected by Dagger.
    @Inject UtilsPrefs mUtilsPrefs;

    // Keeps instance of QuakeCursorLoaderFactory. Injected by Dagger.
    @Inject QuakeCursorLoaderFactory mQuakeCursorLoaderFactory;

    // RecyclerView for the list of earthquakes
    @BindView(R.id.quake_recycler_view) RecyclerView mQuakeRecyclerView;

    // Empty view text (displayed when there is no data for RecyclerView)
    @BindView(R.id.empty_view) TextView mEmptyView;

    // Keeps Unbinder object to properly unbind views in onDestroyView of the fragment
    private Unbinder mUnbinder;

    // Adapter for the RecyclerView
    private QuakeAdapter mQuakeAdapter;

    // Listener to LoaderManager callbacks for quake list loader
    private QuakeListCursorLoaderListener mQuakeListCursorLoaderListener;

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

        // Inject SharedPreference instance into this fragment field
        YellowstoneApp.getAppComponent().inject(this);

        // Create new quake list loader listener
        mQuakeListCursorLoaderListener = new QuakeListCursorLoaderListener();
    }

    // Best practice to initialize a loader is in the fragment's onActivityCreated method,
    // but we do it in onResume, because we must reload data every time the fragment becomes visible
    // (user may return from Settings after changing query parameters).
    @Override
    public void onResume() {
        super.onResume();

        // Get reference to the LoaderManager
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();

        // Initialize quake list loader and set a listener for loader callbacks.
        // restartLoader always reloads data.
        loaderManager.restartLoader(QUAKE_LIST_LOADER_ID, null, mQuakeListCursorLoaderListener);

        // Fetching data from the network is triggered in host activity's onCreate method.
        // After fetching data from the network is complete, UI gets updated automatically. How?
        // In quake ContentProvider query(...) method we set notification URI
        // in the returning cursor by calling cursor.setNotificationUri(...).
        // Quake CursorLoader (that loads data from the quake table) gets this cursor back
        // and registers an observer in ContentResolver.
        // When QuakeFetcher deletes and writes data into quake table,
        // ContentProvider notifies ContentResolver about changes by calling
        // getContext().getContentResolver().notifyChange(uri, null) in insert(...) and delete(...).
        // ContentResolver notifies all registered observers.
        // Observer, registered by CursorLoader, forces it to load new data.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create new adapter for the RecyclerView
        mQuakeAdapter = new QuakeAdapter();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quake_list, container, false);

        // Bind views and save reference to Unbinder object
        mUnbinder = ButterKnife.bind(this, v);

        // Set LinearLayoutManager for our RecyclerView (we need vertical scroll list)
        mQuakeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Attach adapter to the RecyclerView
        mQuakeRecyclerView.setAdapter(mQuakeAdapter);

        // If there are no previously fetched quakes
        if (mUtilsPrefs.isPreviouslyFetchedQuakeNotExist()) {
            // If there is no network connection
            if (UtilsNet.isNetworkNotAvailableAndConnected(getActivity())) {
                // Display error

                // Hide RecyclerView
                mQuakeRecyclerView.setVisibility(View.GONE);

                // Display empty view
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            // Otherwise display RecyclerView with data from USGS server

            // Display RecyclerView
            mQuakeRecyclerView.setVisibility(View.VISIBLE);

            // Hide empty view
            mEmptyView.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // This is recommended to do here when using Butterknife in fragments
        mUnbinder.unbind();
    }

    // === Inner classes =====================

    // ViewHolder for our RecyclerView with list of earthquakes
    // Our ViewHolder implements View.OnClickListener to handle clicks on list items
    class QuakeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Stores earthquake for this ViewHolder
        private Quake mQuake;

        // TextView for the magnitude of the earthquake
        @BindView(R.id.magnitude) public TextView mMagnitudeTextView;

        // TextView for the location of the earthquake
        @BindView(R.id.location) public TextView mLocationTextView;

        // TextView for the date of the earthquake
        @BindView(R.id.date) public TextView mDateTextView;

        public QuakeHolder(View itemView) {
            super(itemView);

            // Our class handles clicks on list items by itself
            itemView.setOnClickListener(this);

            // Get access to TextViews in itemView
            ButterKnife.bind(this, itemView);
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
        private Cursor mCursor;

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
            Quake quake = Quake.getQuakeFromCursor(mCursor);

            // Set ViewHolder of list item according to earthquake at "position"
            holder.bindQuake(quake);
        }

        // Return number of elements in RecyclerView
        @Override
        public int getItemCount() {
            // If the cursor is null, then number of items is 0
            if (null == mCursor) {
                return 0;
            }

            // Otherwise return number of rows in the cursor
            return mCursor.getCount();
        }

        // Swap cursor with new one
        public void swapCursor(Cursor newCursor) {
            // Create new quake cursor wrapper upon the passed in cursor
            mCursor = newCursor;

            // Notify RecyclerView, that data has changed
            notifyDataSetChanged();
        }
    }


    // Listens to LoaderManager callbacks for quake list loader
    private class QuakeListCursorLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

        // Returns new quake list loader
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // Create and return new cursor loader that loads quakes from quake table
            return mQuakeCursorLoaderFactory.createQuakeCursorLoader();
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
