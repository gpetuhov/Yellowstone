package com.gpetuhov.android.yellowstone.data;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.gpetuhov.android.yellowstone.Quake;
import com.gpetuhov.android.yellowstone.QuakeLab;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;


// Test database
public class TestDb extends AndroidTestCase {

    public void testInsertQuake() throws Exception {

        // Get reference to writable database
        SQLiteDatabase db = new YellowstoneDbHelper(mContext).getWritableDatabase();

        // Clear quake table
        db.delete(QuakeEntry.TABLE_NAME, null, null);

        // Create dummy data for test quake
        String ids = "1234567890";
        double magnitude = 2;
        String location = "Test location";
        double latitude = 0;
        double longitude = 0;
        double depth = -5;
        long time = 1000000;
        String url = "Some URL address";

        // Create test Quake object with dummy data
        Quake testQuake = new Quake(ids, magnitude, location, time, url, latitude, longitude, depth);

        // Create ContentValues of test quake
        ContentValues testValues = QuakeLab.getContentValues(testQuake);

        // Insert ContentValues into database and get a row ID back
        long quakeRowId;
        quakeRowId = db.insert(QuakeEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        // SQLiteDatabase.insert returns -1 if an error occured.
        assertTrue("Error: test quake was not inserted", quakeRowId != -1);

        // Query the database and receive a cursor wrapper back
        QuakeCursorWrapper cursor = QuakeLab.queryQuakes(
                db,
                QuakeEntry.COLUMN_IDS + " = ?",
                new String[] { testQuake.getId() }
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from quake query", cursor.moveToFirst() );

        // Get quake from the cursor
        Quake quakeFromDb = cursor.getQuake();

        // Check if testQuake and quake from database are equal
        assertTrue("Error: Quake query validation failed", testQuake.equals(quakeFromDb));

        // Move the cursor to demonstrate that there is only one record in the cursor
        assertFalse( "Error: More than one record returned from quake query",
                cursor.moveToNext() );

        // Close cursor
        cursor.close();

        // Delete test quake from database and save number of deleted rows
        int deletedRowsNum;
        deletedRowsNum = db.delete(
                QuakeEntry.TABLE_NAME,
                QuakeEntry.COLUMN_IDS + " = ?",
                new String[] { testQuake.getId() }
        );

        // Check if 1 row was deleted
        assertTrue("Error in test quake delete operation", deletedRowsNum == 1);

        // Close database
        db.close();
    }
}
