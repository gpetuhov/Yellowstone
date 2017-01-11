package com.gpetuhov.android.yellowstone.utils;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.gpetuhov.android.yellowstone.Quake;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;

import java.util.ArrayList;
import java.util.List;

// Utilities for getting quake list from quake table
public class UtilsQuakeList {

    // Keeps instance of content resolver
    private ContentResolver mContentResolver;

    public UtilsQuakeList(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    // Return list of all quakes from quakes table
    public List<Quake> getQuakes() {
        return getQuakeList(null);
    }

    // Return Quake object with specified ID from the quakes table
    public Quake getQuake(long quakeDbId) {
        // Get list of one quake with specified ID from quakes table
        List<Quake> quakes = getQuakeList(quakeDbId);

        if (quakes.size() > 0) {
            return quakes.get(0);
        } else {
            return null;
        }
    }

    // If quakeDbId is specified, return quake list with one quake with this ID from the quakes table,
    // otherwise (quakeDbId == null) return list of all quakes.
    private List<Quake> getQuakeList(Long quakeDbId) {

        // Create new empty list of quakes
        List<Quake> quakes = new ArrayList<>();

        Uri uri;

        // If ID of the quake is not specified
        if (null == quakeDbId) {
            // Create URI for all quakes table
            uri = QuakeEntry.CONTENT_URI;
        } else {
            // Otherwise create URI for quake with specified ID
            uri = Uri.withAppendedPath(QuakeEntry.CONTENT_URI, String.valueOf(quakeDbId));
        }

        // Get content resolver for the context, query quakes table
        // and save received cursor.
        Cursor cursor = mContentResolver.query(
                uri,    // URI
                null,   // Projection
                null,   // Selection
                null,   // Selection arguments
                null    // Sort order
        );

        // If the cursor is null, return empty list of quakes
        if (cursor == null) {
            return quakes;
        }

        try {
            // Move to the first row of the cursor
            cursor.moveToFirst();

            // While we didn't move after the last row of the cursor
            while (!cursor.isAfterLast()) {

                // Extract Quake object from the cursor row and add it to list of quakes
                quakes.add(Quake.getQuakeFromCursor(cursor));

                // Move to the next row of the cursor
                cursor.moveToNext();
            }

        } finally {
            // Always close cursors, if not closed, to prevent memory leaks
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }

        return quakes;
    }
}
