package com.gpetuhov.android.yellowstone;


// Utilities class
public class QuakeUtils {

    // Caldera location on map
    public static final String CALDERA_LATITUDE = "44.5";       // Latitude of the center of caldera (degrees)
    public static final String CALDERA_LONGITUDE = "-110.6";    // Longitude of the center of caldera (degrees)
    public static final String CALDERA_RADIUS = "40";           // Radius of caldera (in kilometers)

    // Return Caldera latitude converted to double
    public static double getCalderaLatDouble() {
        return Double.parseDouble(CALDERA_LATITUDE);
    }

    // Return Caldera longitude converted to double
    public static double getCalderaLngDouble() {
        return Double.parseDouble(CALDERA_LONGITUDE);
    }

}
