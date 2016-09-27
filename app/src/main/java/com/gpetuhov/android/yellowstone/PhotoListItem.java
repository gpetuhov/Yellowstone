package com.gpetuhov.android.yellowstone;


import android.net.Uri;

// Stores one recent photo from Yellowstone
public class PhotoListItem {

    // Url for building link to the webpage of the photo
    public static final String FLICKR_PHOTO_URL = "http://www.flickr.com/photos/";

    // Photo title
    private String mCaption;

    // Photo ID
    private String mId;

    // Photo URL (link to the image)
    private String mUrl;

    // Owner of the photo
    private String mOwner;

    // Getters and setters

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    // Return URL of the webpage of the photo
    public Uri getPhotoPageUri() {

        // Links to webpages of photos on Flickr look like: http://www.flickr.com/photos/owner/id
        return Uri.parse(FLICKR_PHOTO_URL)
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }
}
