package com.gpetuhov.android.yellowstone;

// Stores information about one earthquake
public class Quake {

    // ID of the earthquake
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

}
