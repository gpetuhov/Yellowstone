package com.gpetuhov.android.yellowstone;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

        // Read all rows from quakes table (SELECT * FROM quake_table_name)
        // and save result in a cursor wrapper.
        QuakeCursorWrapper cursor = queryQuakes(mDatabase, null, null);

        try {
            // Move to the first row of the cursor
            cursor.moveToFirst();

            // While we didn't move after the last row of the cursor
            while (!cursor.isAfterLast()) {

                // Extract Quake object from the cursor row and add it to list of quakes
                quakes.add(cursor.getQuake());

                // Move to the next row of the cursor
                cursor.moveToNext();
            }

        } finally {
            // Always close cursor to prevent memory leaks
            cursor.close();
        }

        return quakes;
    }


    // Set new list of earthquakes
    public void setQuakes(List<Quake> quakes) {

        // Delete all rows from quake table (remove previously fetched data)
        mDatabase.delete(QuakeEntry.TABLE_NAME, null, null);

        // For each earthquake in the list
        for (Quake quake : quakes) {
            // Get content values for this earthquake
            // and insert content values into quake table
            mDatabase.insert(QuakeEntry.TABLE_NAME, null, getContentValues(quake));
        }
    }


    // Return Quake object with specified ID
    public Quake getQuake(String quakeId) {

        // Query database and save result in a cursor wrapper
        QuakeCursorWrapper cursor = queryQuakes(    // SELECT * FROM quake_table_name
                mDatabase,
                QuakeEntry.COLUMN_IDS + " = ?",     // WHERE ids =
                new String[] { quakeId }            // quakeId
                                                    // (here we have one argument of WHERE clause,
                                                    // which is passed in as new String array with one element)
        );

        try {
            // If there are no rows in the response cursor
            if (cursor.getCount() == 0) {
                // There is no earthquake with specified ID in the database, nothing to return
                return null;
            }

            // Move to the first row of the cursor
            cursor.moveToFirst();

            // Extract Quake object from the cursor row and return it
            return cursor.getQuake();

        } finally {
            // Always close cursor to prevent memory leaks
            cursor.close();
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
