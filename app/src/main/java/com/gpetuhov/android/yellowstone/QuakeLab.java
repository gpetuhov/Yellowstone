package com.gpetuhov.android.yellowstone;


import android.content.Context;

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

    // Stores list of earthquakes
    private List<Quake> mQuakes;

    // Return link to QuakeLab instance
    public static QuakeLab get(Context context) {

        // If instance of QuakeLab does not exist, create new instance of QuakeLab
        if (sQuakeLab == null) {
            sQuakeLab = new QuakeLab(context);
        }

        return sQuakeLab;
    }

    private QuakeLab(Context context) {
        // Create new empty list to store earthquakes
        mQuakes = new ArrayList<>();
    }

    // Return list of earthquakes
    public List<Quake> getQuakes() {
        return mQuakes;
    }

    // Set new list of earthquakes
    public void setQuakes(List<Quake> quakes) {
        mQuakes = quakes;
    }

    // Return Quake object with specified ID
    public Quake getQuake(String quakeId) {

        // For each earthquake in the list check if ID equals specified ID
        // If true, return current earthquake
        for (Quake quake : mQuakes) {
            if (quake.getId().equals(quakeId)) {
                return quake;
            }
        }

        // There is no earthquake with specified ID in the list
        // Nothing to return
        return null;
    }

}
