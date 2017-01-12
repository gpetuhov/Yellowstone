package com.gpetuhov.android.yellowstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gpetuhov.android.yellowstone.utils.UtilsNet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


// Fragment contains list of recent photos from Yellowstone
// This fragment implements LoaderManager callbacks to update UI with data from loader.
public class PhotoListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<PhotoListItem>> {

    // Number of columns in RecyclerView with list of photos
    public static final int PHOTO_LIST_COLUMNS_NUM = 3;

    // Loader ID
    public static final int PHOTO_LOADER_ID = 3;

    // Stores list of photos
    private List<PhotoListItem> mItems = new ArrayList<>();

    // RecyclerView for the list of photos
    private RecyclerView mPhotoRecyclerView;

    // TextView for empty view (when there is no internet connection)
    private TextView mPhotoEmptyView;


    // Best practice to initialize a loader is in the fragment's onActivityCreated method
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // If there is a network connection, fetch data
        if (UtilsNet.isNetworkAvailableAndConnected(getActivity())) {

            // Get reference to the LoaderManager
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();

            // Initialize loader and set this fragment as a listener for loader callbacks
            // If the loader with the passed ID exists and the data is ready,
            // initLoader immediately pushes data to onLoadFinished callback method.
            // If not, loader is created and starts loading data.
            loaderManager.initLoader(PHOTO_LOADER_ID, null, this);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fragment instance is not destroyed on orientation change
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photo_list, container, false);

        // Get reference to RecyclerView
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_list_recycler_view);

        // Set GridLayoutManager and number of columns for our RecyclerView
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), PHOTO_LIST_COLUMNS_NUM));

        // Get access to TextView for empty view
        mPhotoEmptyView = (TextView) v.findViewById(R.id.photo_list_empty_view);

        // If there is a network connection, display RecyclerView with photos
        if (UtilsNet.isNetworkAvailableAndConnected(getActivity())) {
            // Display RecyclerView
            mPhotoRecyclerView.setVisibility(View.VISIBLE);

            // Hide empty view
            mPhotoEmptyView.setVisibility(View.GONE);

            // Set adapter for RecyclerView
            setupAdapter();
        } else {
            // Otherwise, display error

            // Hide RecyclerView
            mPhotoRecyclerView.setVisibility(View.GONE);

            // Display empty view
            mPhotoEmptyView.setVisibility(View.VISIBLE);
        }

        return v;
    }


    // Set adapter for the RecyclerView
    private void setupAdapter() {

        // If the fragment is added to a parent activity
        if (isAdded()) {

            // Create new adapter with the list of photos
            // and set it as adapter for the RecyclerView
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }


    // Returns new loader
    @Override
    public Loader<List<PhotoListItem>> onCreateLoader(int id, Bundle args) {
        return new PhotoLoader(getActivity());
    }

    // When load is finished, saves the fetched data and updates UI
    @Override
    public void onLoadFinished(Loader<List<PhotoListItem>> loader, List<PhotoListItem> data) {

        // Store fetched list of photos in mItems field
        mItems = data;

        // Create new adapter for RecyclerView with new list of photos
        setupAdapter();
    }

    // Method is called when data from loader is no longer valid
    @Override
    public void onLoaderReset(Loader<List<PhotoListItem>> loader) {
    }


    // === Inner classes =====================

    // ViewHolder for the RecyclerView with list of photos
    // Our ViewHolder implements View.OnClickListener to handle clicks on list items
    private class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // Stores photo details for this ViewHolder
        private PhotoListItem mPhotoListItem;

        // ImageView for the image of the photo
        public ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);

            // Our class handles clicks on list items by itself
            itemView.setOnClickListener(this);

            // Get access to ImageView in itemView
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_list_image_view);
        }

        public void bindPhoto(PhotoListItem photo) {

            // Store photo details
            mPhotoListItem = photo;

            // Download photo image from its URL and place it into ImageView
            // using Picasso library.
            Picasso.with(getActivity())
                    .load(mPhotoListItem.getUrl())
                    .into(mItemImageView);
        }

        // Handle clicks on list items
        @Override
        public void onClick(View v) {

            // Build photo webpage URl and create new intent for PhotoPageActivity with it
            Intent intent = PhotoPageActivity.newIntent(getActivity(), mPhotoListItem.getPhotoPageUri());

            // Start PhotoPageActivity with this intent
            startActivity(intent);
        }
    }


    // Adapter for the RecyclerView with list of photos
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        // Empty list for the list of photos
        private List<PhotoListItem> mItems;

        public PhotoAdapter(List<PhotoListItem> items) {
            mItems = items;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Get LayoutInflater from parent activity
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            // Create view for one list item from item layout
            View view = layoutInflater.inflate(R.layout.list_item_photo, parent, false);

            // Create ViewHolder with inflated view for one list item
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            // Get photo at "position" from list of photos
            PhotoListItem photo = mItems.get(position);

            // Set ViewHolder of list item according to photo at "position"
            holder.bindPhoto(photo);
        }

        @Override
        public int getItemCount() {
            // Return size of list of photos
            return mItems.size();
        }
    }

}
