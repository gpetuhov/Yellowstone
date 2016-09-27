package com.gpetuhov.android.yellowstone;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    // Return new intent with extra data to start this activity
    public static Intent newIntent (Context context, Uri photoPageUri) {

        // Create explicit intent to start this activity
        Intent intent = new Intent(context, PhotoPageActivity.class);

        // Put webpage URL as extra data in intent
        intent.setData(photoPageUri);

        return intent;
    }

    // Create fragment for this activity
    @Override
    protected Fragment createFragment() {

        // Get webpage URL from the intent that started this activity
        // And create new fragment with this URL
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
}
