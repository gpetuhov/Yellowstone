package com.gpetuhov.android.yellowstone.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gpetuhov.android.yellowstone.Quake;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;


// Builds Quake object from a cursor row
public class QuakeCursorWrapper extends CursorWrapper {

    public QuakeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    // Return Quake object with data extracted from a cursor row
    public Quake getQuake() {

        // Extract columns from a cursor row and save them to corresponding variables
        String ids = getString(getColumnIndex(QuakeEntry.COLUMN_IDS));
        double magnitude = getDouble(getColumnIndex(QuakeEntry.COLUMN_MAGNITUDE));
        String location = getString(getColumnIndex(QuakeEntry.COLUMN_LOCATION));
        double latitude = getDouble(getColumnIndex(QuakeEntry.COLUMN_LATITUDE));
        double longitude = getDouble(getColumnIndex(QuakeEntry.COLUMN_LONGITUDE));
        double depth = getDouble(getColumnIndex(QuakeEntry.COLUMN_DEPTH));
        long time = getLong(getColumnIndex(QuakeEntry.COLUMN_TIME));
        String url = getString(getColumnIndex(QuakeEntry.COLUMN_URL));

        // Create new Quake object with data extracted from a cursor row
        Quake quake = new Quake(ids, magnitude, location, time, url, latitude, longitude, depth);

        return quake;
    }
}
