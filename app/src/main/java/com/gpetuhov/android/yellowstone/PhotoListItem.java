package com.gpetuhov.android.yellowstone;


// Stores one recent photo from Yellowstone
public class PhotoListItem {

    // Photo title
    private String mCaption;

    // Photo ID
    private String mId;

    // Photo URL (webpage with photo details)
    private String mUrl;


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
}
