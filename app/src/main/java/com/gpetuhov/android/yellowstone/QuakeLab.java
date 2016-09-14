package com.gpetuhov.android.yellowstone;


import android.content.Context;


// Currently this class is not used
// For future purposes

// Stores list of earthquakes
// This class uses Singleton Design Pattern
// so that there can be only one instance of this class
public class QuakeLab {

    // Link to the instance of the class
    private static QuakeLab sQuakeLab;

    public QuakeLab(Context context) {
    }

    // Return link to QuakeLab instance
    public static QuakeLab get(Context context) {

        // If instance of QuakeLab does not exist, create new instance of QuakeLab
        if (sQuakeLab == null) {
            sQuakeLab = new QuakeLab(context);
        }

        return sQuakeLab;
    }

}
