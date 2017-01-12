package com.gpetuhov.android.yellowstone.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;

import com.gpetuhov.android.yellowstone.R;
import com.gpetuhov.android.yellowstone.data.YellowstoneContract.QuakeEntry;
import com.gpetuhov.android.yellowstone.utils.UtilsPrefs;

public class QuakeCursorLoaderFactory {

    private Context mContext;
    private UtilsPrefs mUtilsPrefs;

    public QuakeCursorLoaderFactory(Context context, UtilsPrefs utilsPrefs) {
        mContext = context;
        mUtilsPrefs = utilsPrefs;
    }

    public CursorLoader createQuakeCursorLoader() {
        // Get magnitude preference value from SharedPreference by the key
        // (default value is value_1 (minimum magnitude = 0, that is all magnitudes)
        String minMagnitude = mUtilsPrefs.getStringFromSharedPreferences(
                mContext.getString(R.string.pref_magnitude_key),
                mContext.getString(R.string.pref_magnitude_value_1));

        // Build selection (WHERE clause)
        String selection = QuakeEntry.COLUMN_MAGNITUDE + " >= ?";    // WHERE mag >=

        // Build selection arguments (arguments of the condition of WHERE clause).
        String[] selectionArgs = new String[] { minMagnitude };

        // Create and return new cursor loader that loads quakes from quake table in the database
        return new CursorLoader(mContext,
                QuakeEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
    }
}
