package com.gpetuhov.android.yellowstone;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gpetuhov.android.yellowstone.data.QuakeCursorWrapper;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;
import com.gpetuhov.android.yellowstone.data.YellowstoneDbHelper;

import java.util.ArrayList;
import java.util.List;


// Currently this class is not used
// For future purposes

// Stores list of earthquakes
// This class uses Singleton Design Pattern
// so that there can be only one instance of this class
public class QuakeLab {

    // Reference to the instance of the class
    private static QuakeLab sQuakeLab;

    // Applicataion context to access database
    private Context mContext;

    // Reference to database with quakes table
    private SQLiteDatabase mDatabase;

    // Return link to QuakeLab instance
    public static QuakeLab get(Context context) {

        // If instance of QuakeLab does not exist, create new instance of QuakeLab
        if (sQuakeLab == null) {
            sQuakeLab = new QuakeLab(context);
        }

        return sQuakeLab;
    }

    private QuakeLab(Context context) {

        // Get application context.
        // We need application context instead of activity context,
        // because QuakeLab lives longer than activities.
        mContext = context.getApplicationContext();

        // Get reference to database with permission to modify data
        mDatabase = new YellowstoneDbHelper(mContext).getWritableDatabase();
    }


    // Return list of earthquakes
    public List<Quake> getQuakes() {

        // Create new empty list of quakes
        List<Quake> quakes = new ArrayList<>();

        // Get content resolver for the application context, query for all quakes
        // and save received cursor.
        Cursor cursor = mContext.getContentResolver().query(
                QuakeEntry.CONTENT_URI, // URI
                null,                   // Projection
                null,                   // Selection
                null,                   // Selection arguments
                null                    // Sort order
        );

        // If the cursor is null, return empty list of quakes
        if (cursor == null) {
            return quakes;
        }

        // Create quake cursor wrapper upon the received cursor
        QuakeCursorWrapper cursorWrapper = new QuakeCursorWrapper(cursor);

        try {
            // Move to the first row of the cursor
            cursorWrapper.moveToFirst();

            // While we didn't move after the last row of the cursor
            while (!cursorWrapper.isAfterLast()) {

                // Extract Quake object from the cursor row and add it to list of quakes
                quakes.add(cursorWrapper.getQuake());

                // Move to the next row of the cursor
                cursorWrapper.moveToNext();
            }

        } finally {
            // Always close cursor to prevent memory leaks
            cursorWrapper.close();
        }

        return quakes;
    }


    // Set new list of earthquakes
    public void setQuakes(List<Quake> quakes) {

        // Get content resolver for the application context
        ContentResolver contentResolver = mContext.getContentResolver();

        // Delete all rows from quake table (remove previously fetched data).
        // Method returns number of rows deleted, but we don't use it.
        contentResolver.delete(QuakeEntry.CONTENT_URI, null, null);

        // For each earthquake in the list
        for (Quake quake : quakes) {
            // Get content values for this earthquake and insert content values into the provider,
            // returning the content URI for this new quake.
            Uri newUri = contentResolver.insert(QuakeEntry.CONTENT_URI, getContentValues(quake));

            // If URI for the new quake is null, stop inserting new quakes into the table and return
            if (newUri == null) {
                return;
            }
        }
    }


    // Return Quake object with specified ID from the database table
    public Quake getQuake(long quakeDbId) {

        // Get content resolver for the application context, query for the quake with specified ID
        // and save received cursor.
        Cursor cursor = mContext.getContentResolver().query(
                Uri.withAppendedPath(QuakeEntry.CONTENT_URI, String.valueOf(quakeDbId)),
                // Build URI (scheme://content_authority/table_name/row_id)
                null,                   // Projection
                null,                   // Selection
                null,                   // Selection arguments
                null                    // Sort order
        );

        // If the cursor is null, return null
        if (cursor == null) {
            return null;
        }

        // Create quake cursor wrapper upon the received cursor
        QuakeCursorWrapper cursorWrapper = new QuakeCursorWrapper(cursor);

        try {
            // If there are no rows in the response cursor
            if (cursorWrapper.getCount() == 0) {
                // There is no earthquake with specified ID in the database, nothing to return
                return null;
            }

            // Move to the first row of the cursor
            cursorWrapper.moveToFirst();

            // Extract Quake object from the cursor row and return it
            return cursorWrapper.getQuake();

        } finally {
            // Always close cursor to prevent memory leaks
            cursorWrapper.close();
        }
    }


    // Return content values to write Quake object into database
    public static ContentValues getContentValues(Quake quake) {

        // Create new content values
        ContentValues values = new ContentValues();

        // Put data from Quake object fields into content values
        values.put(QuakeEntry.COLUMN_IDS, quake.getId());
        values.put(QuakeEntry.COLUMN_MAGNITUDE, quake.getMagnitude());
        values.put(QuakeEntry.COLUMN_LOCATION, quake.getLocation());
        values.put(QuakeEntry.COLUMN_LATITUDE, quake.getLatitude());
        values.put(QuakeEntry.COLUMN_LONGITUDE, quake.getLongitude());
        values.put(QuakeEntry.COLUMN_DEPTH, quake.getDepth());
        values.put(QuakeEntry.COLUMN_TIME, quake.getTimeInMilliseconds());
        values.put(QuakeEntry.COLUMN_URL, quake.getUrl());

        return values;
    }


    // Query database and return response cursor.
    // Where clause and arguments of where clause are passed in as parameters.
    public static QuakeCursorWrapper queryQuakes(SQLiteDatabase db, String whereClause, String[] whereArgs) {

        // Execute SELECT statement and save the cursor that contains the response
        Cursor cursor = db.query(
                QuakeEntry.TABLE_NAME,  // Table name
                null,                   // Columns - if null, select all columns (SELECT * FROM table_name)
                whereClause,            // WHERE clause
                whereArgs,              // Arguments of WHERE clause (passed separately to prevent SQL injection)
                null,                   // GROUP BY statement
                null,                   // HAVING clause
                null                    // ORDER BY clause
        );

        // Create new cursor wrapper upon the response cursor and return it
        return new QuakeCursorWrapper(cursor);
    }
}
