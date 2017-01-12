package com.gpetuhov.android.yellowstone;

import android.content.ContentValues;
import android.database.Cursor;

import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Stores information about one earthquake.
// Implements Serializable to be passed between activities and fragments.
public class Quake implements Serializable {

    // ID of the earthquake that comes from USGS server
    private String mId;

    // Magnitude of the earthquake
    private double mMagnitude;

    // Location name of the earthquake
    private String mLocation;

    // Latitude of the earthquake
    private double mLatitude;

    // Longitude of the earthquake
    private double mLongitude;

    // Depth of the earthquake (in kilometers)
    private double mDepth;

    // Time of the earthquake (in milliseconds)
    private long mTimeInMilliseconds;

    // Website URL of the earthquake
    private String mUrl;

    // Return Quake object with data extracted from a cursor row
    public static Quake getQuakeFromCursor(Cursor cursor) {

        // Extract columns from a cursor row and save them to corresponding variables
        String ids = cursor.getString(cursor.getColumnIndex(QuakeEntry.COLUMN_IDS));
        double magnitude = cursor.getDouble(cursor.getColumnIndex(QuakeEntry.COLUMN_MAGNITUDE));
        String location = cursor.getString(cursor.getColumnIndex(QuakeEntry.COLUMN_LOCATION));
        double latitude = cursor.getDouble(cursor.getColumnIndex(QuakeEntry.COLUMN_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(QuakeEntry.COLUMN_LONGITUDE));
        double depth = cursor.getDouble(cursor.getColumnIndex(QuakeEntry.COLUMN_DEPTH));
        long time = cursor.getLong(cursor.getColumnIndex(QuakeEntry.COLUMN_TIME));
        String url = cursor.getString(cursor.getColumnIndex(QuakeEntry.COLUMN_URL));

        // Create new Quake object with data extracted from a cursor row
        Quake quake = new Quake(ids, magnitude, location, time, url, latitude, longitude, depth);

        return quake;
    }

    // Return list of quakes from cursor
    public static List<Quake> getQuakeListFromCursor(Cursor cursor) {
        // Create new empty list of quakes
        List<Quake> quakes = new ArrayList<>();

        // If the cursor is null, return empty list of quakes
        if (null == cursor) {
            return quakes;
        }

        // Move to the first row of the cursor
        cursor.moveToFirst();

        // While we didn't move after the last row of the cursor
        while (!cursor.isAfterLast()) {
            // Extract Quake object from the cursor row and add it to list of quakes
            quakes.add(getQuakeFromCursor(cursor));

            // Move to the next row of the cursor
            cursor.moveToNext();
        }

        return quakes;

        // Do not close cursor, because cursor is managed by CursorLoader
    }

    public Quake(String id, double magnitude, String location, long timeInMilliseconds,
                 String url, double latitude, double longitude, double depth) {
        mId = id;
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
        mLatitude = latitude;
        mLongitude = longitude;
        mDepth = depth;
    }

    // Return the ID of the earthquake
    public String getId() {
        return mId;
    }

    // Return the magnitude of the earthquake
    public double getMagnitude() {
        return mMagnitude;
    }

    // Return the location name of the earthquake
    public String getLocation() {
        return mLocation;
    }

    // Return the time of the earthquake
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    // Return the website URL to find more information about the earthquake
    public String getUrl() {
        return mUrl;
    }

    // Return latitude of the earthquake
    public double getLatitude() {
        return mLatitude;
    }

    // Return longitude of the earthquake
    public double getLongitude() {
        return mLongitude;
    }

    // Return depth of the earthquake
    public double getDepth() {
        return mDepth;
    }

    // Return magnitude in String format
    public String getFormattedMagnitude() {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(mMagnitude);
    }

    // Return date in String format
    public String getFormattedDate() {
        // Create a new Date object from the time in milliseconds of the earthquake
        Date dateObject = new Date(mTimeInMilliseconds);

        // Create date format (ex. 2016-09-15 09:42:22)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        // Format Date object to string
        String dateString = dateFormat.format(dateObject) + " (UTC)";

        // Return date string
        return dateString;
    }

    // Return depth in String format
    public String getFormattedDepth() {
        DecimalFormat depthFormat = new DecimalFormat("0.0");
        return depthFormat.format(mDepth);
    }

    // Return latitude in String format
    public String getFormattedLatitude() {
        DecimalFormat latitudeFormat = new DecimalFormat("0.000");
        return latitudeFormat.format(mLatitude);
    }

    // Return longitude in String format
    public String getFormattedLongitude() {
        DecimalFormat longitudeFormat = new DecimalFormat("0.000");
        return longitudeFormat.format(mLongitude);
    }

    // Return content values to write Quake object into database
    public ContentValues getQuakeContentValues() {

        // Create new content values
        ContentValues values = new ContentValues();

        // Put data from Quake object fields into content values
        values.put(QuakeEntry.COLUMN_IDS, getId());
        values.put(QuakeEntry.COLUMN_MAGNITUDE, getMagnitude());
        values.put(QuakeEntry.COLUMN_LOCATION, getLocation());
        values.put(QuakeEntry.COLUMN_LATITUDE, getLatitude());
        values.put(QuakeEntry.COLUMN_LONGITUDE, getLongitude());
        values.put(QuakeEntry.COLUMN_DEPTH, getDepth());
        values.put(QuakeEntry.COLUMN_TIME, getTimeInMilliseconds());
        values.put(QuakeEntry.COLUMN_URL, getUrl());

        return values;
    }

    // Check if value of fields of this Quake object equal to values of fields of passed Quake object
    @Override
    public boolean equals(Object o) {

        // If passed object is instance of Quake class
        if (o instanceof Quake) {

            // Cast passed object to Quake object
            Quake quake = (Quake) o;

            // Get values of fields of passed in Quake object
            String ids = quake.getId();
            double magnitude = quake.getMagnitude();
            String location = quake.getLocation();
            double latitude = quake.getLatitude();
            double longitude = quake.getLongitude();
            double depth = quake.getDepth();
            long time = quake.getTimeInMilliseconds();
            String url = quake.getUrl();

            // If any of the fields are not equal, then quakes are not equal. Return false
            if (!ids.equals(getId())) { return false; }
            if (magnitude != getMagnitude()) { return false; }
            if (!location.equals(getLocation())) { return false; }
            if (latitude != getLatitude()) { return false; }
            if (longitude != getLongitude()) { return false; }
            if (depth != getDepth()) { return false; }
            if (time != getTimeInMilliseconds()) { return false; }
            if (!url.equals(getUrl())) { return false; }

            // Otherwise quakes are equal. Return true
            return true;

        } else {
            // Otherwise passed object is not instance of Quake class,
            // so this quake definitely is not equal to it.
            return false;
        }
    }
}
