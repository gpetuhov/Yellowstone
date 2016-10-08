package com.gpetuhov.android.yellowstone.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;


// Quake content provider.
// Abstraction layer between quake database and UI.
public class QuakeProvider extends ContentProvider {

    // Tag for the log messages
    public static final String LOG_TAG = QuakeProvider.class.getSimpleName();

    // URI matcher code for the content URI for the quakes table
    private static final int QUAKES = 100;

    // URI matcher code for the content URI for a single quake in the quakes table
    private static final int QUAKE_ID = 101;

    // UriMatcher object to match a content URI to a corresponding code.
    // The input passed into the constructor represents the code to return for the root URI.
    // It's common to use NO_MATCH as the input for this case.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    // (order of execution: 1. Static initializer, 2. Instance initializer, 3. Constructor)
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // Add URI for the quakes table
        sUriMatcher.addURI(
                YellowstoneContract.CONTENT_AUTHORITY,
                YellowstoneContract.PATH_QUAKES,
                QUAKES
        );

        // Add URI for a single quake in the quakes table.
        sUriMatcher.addURI(
                YellowstoneContract.CONTENT_AUTHORITY,
                YellowstoneContract.PATH_QUAKES + "/#",
                QUAKE_ID
        );
    }


    // Reference to database helper object
    private YellowstoneDbHelper mDbHelper;


    // Initialize the provider and the database helper object
    @Override
    public boolean onCreate() {

        // Get reference to database helper object
        mDbHelper = new YellowstoneDbHelper(getContext());

        return true;
    }

    // Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Match URI to a code and save it to a variable
        int match = sUriMatcher.match(uri);

        // Depending of the code query for all quakes or for the specific quake
        switch (match) {
            case QUAKES:
                // Query for all quakes

                // Query database and save result in a cursor
                cursor = database.query(        // SELECT * FROM quakes
                        QuakeEntry.TABLE_NAME,  // Table name
                        projection,             // Columns - if null, select all columns (SELECT * FROM table_name)
                        selection,              // WHERE clause
                        selectionArgs,          // Arguments of WHERE clause (passed separately to prevent SQL injection)
                        null,                   // GROUP BY statement
                        null,                   // HAVING clause
                        sortOrder               // ORDER BY clause
                );

                return cursor;

            case QUAKE_ID:
                // Query for the specific quake

                // Build selection (WHERE clause)
                selection = QuakeEntry._ID + " = ?";

                // Build selection arguments (arguments of the condition of WHERE clause).
                // To do this, we extract the last part of URI into long variable,
                // then get String value from it and create new array of 1 element,
                // containing this String value.
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Query database and save result in a cursor
                cursor = database.query(        // SELECT * FROM quakes WHERE _id = dbId
                        QuakeEntry.TABLE_NAME,  // Table name
                        projection,             // Columns - if null, select all columns (SELECT * FROM table_name)
                        selection,              // WHERE clause
                        selectionArgs,          // Arguments of WHERE clause (passed separately to prevent SQL injection)
                        null,                   // GROUP BY statement
                        null,                   // HAVING clause
                        sortOrder               // ORDER BY clause
                );

                return cursor;

            default:
                // URI didn't match any of the codes. Nothing to return.
                return null;
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

}
