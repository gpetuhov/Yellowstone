package com.gpetuhov.android.yellowstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;


// Manages database creation and version management
public class YellowstoneDbHelper extends SQLiteOpenHelper {

    // Name of the database file
    private static final String DATABASE_NAME = "yellowstone.db";

    // Database version
    private static final int DATABASE_VERSION = 1;

    public YellowstoneDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the quakes table
        String SQL_CREATE_QUAKES_TABLE = "CREATE TABLE " + QuakeEntry.TABLE_NAME + " ("
                + QuakeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QuakeEntry.COLUMN_IDS + " TEXT, "
                + QuakeEntry.COLUMN_MAGNITUDE + " REAL, "
                + QuakeEntry.COLUMN_LOCATION + " TEXT, "
                + QuakeEntry.COLUMN_LATITUDE + " REAL, "
                + QuakeEntry.COLUMN_LONGITUDE + " REAL, "
                + QuakeEntry.COLUMN_DEPTH + " REAL, "
                + QuakeEntry.COLUMN_TIME + " INTEGER, "
                + QuakeEntry.COLUMN_URL + " TEXT "
                + " );";

        // Execute the SQL statement to create quake table
        db.execSQL(SQL_CREATE_QUAKES_TABLE);
    }


    // Method is called when the database needs to be upgraded (when database version changes)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        // Create a String that contains the SQL statement to delete the quakes table
        String SQL_DELETE_QUAKES_TABLE = "DROP TABLE IF EXISTS " + QuakeEntry.TABLE_NAME;

        // Execute the SQL statement to delete quake table
        db.execSQL(SQL_DELETE_QUAKES_TABLE);

        // Call onCreate method to create new quake table
        onCreate(db);
    }
}