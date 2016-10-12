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

        // Depending on the code, query for all quakes or for the specific quake
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

                break;

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

                break;

            default:
                // URI didn't match any of the codes. Nothing to return.
                return null;
        }

        // Set notification URI for the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return cursor
        return cursor;
    }

    // Insert new data into the provider with the given ContentValues
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Match URI to a code and save it to a variable
        final int match = sUriMatcher.match(uri);

        // Check if thw URI addresses the whole quake table
        switch (match) {
            case QUAKES:
                // Insert new quake in the quake table
                return insertQuake(uri, contentValues);

            default:
                // URI addresses specific row or didn't match any of the codes.
                // Cannot insert new quake.
                return null;
        }
    }

    // Insert quake into the quake table with the given content values.
    // Return the new content URI for that specific row in the table.
    private Uri insertQuake(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert new quake into quake table and get new row ID
        long newRowId = database.insert(QuakeEntry.TABLE_NAME, null, values);

        // If new row ID is -1, an error occurred
        if (newRowId == -1) {
            // Return null instead of the URI of the new row
            return null;
        }

        // Notify listeners, that data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Build new row URI by appending new row ID to the quake table URI and return it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // We don't use this method in our app, so we leave it blank.
        return 0;
    }

    // Delete the data at the given selection and selection arguments.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Match URI to a code and save it to a variable
        final int match = sUriMatcher.match(uri);

        // Number of rows deleted
        int numRowsDeleted;

        // Depending on the code, delete all quakes or for the specific quake
        switch (match) {
            case QUAKES:
                // Delete all rows that match the selection and selection args
                // and return number of rows deleted.
                numRowsDeleted = database.delete(QuakeEntry.TABLE_NAME, selection, selectionArgs);

                break;

            case QUAKE_ID:
                // Delete a single row given by the ID in the URI

                // Build selection (WHERE clause)
                selection = QuakeEntry._ID + " = ?";    // WHERE _id =

                // Build selection arguments (arguments of the condition of WHERE clause).
                // To do this, we extract the last part of URI into long variable,
                // then get String value from it and create new array of 1 element,
                // containing this String value.
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Perform delete operation and return number of rows deleted
                numRowsDeleted = database.delete(QuakeEntry.TABLE_NAME, selection, selectionArgs);

                break;

            default:
                // URI didn't match any of the codes. No rows were deleted.
                numRowsDeleted = 0;
        }

        // If some rows were deleted
        if (numRowsDeleted != 0) {
            // Notify listeners, that data has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return number of rows deleted
        return numRowsDeleted;
    }

    // Returns the MIME type of data for the content URI
    @Override
    public String getType(Uri uri) {

        // Match URI to a code and save it to a variable
        final int match = sUriMatcher.match(uri);

        // Depending on the code, return MIME type of a list of quakes or a single quake
        switch (match) {
            case QUAKES:
                // Return MIME type of a list of quakes
                return QuakeEntry.CONTENT_LIST_TYPE;

            case QUAKE_ID:
                // Return MIME type of a single quake
                return QuakeEntry.CONTENT_ITEM_TYPE;

            default:
                // URI didn't match any of the codes. Return null.
                return null;
        }
    }

}
