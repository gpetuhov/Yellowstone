package com.gpetuhov.android.yellowstone;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


// Fragment for webpage of the photo
public class PhotoPageFragment extends Fragment {

    // Key for fragment's argument with photo webpage URL
    public static final String ARG_URI = "photo_page_url";

    // ProgressBar maximum value
    public static final int PROGRESS_BAR_MAX_VALUE = 100;

    // URL of the photo webpage
    private Uri mUri;

    // WebView for displaying photo webpage
    private WebView mWebView;

    // ProgressBar for displaying webpage download progress
    private ProgressBar mProgressBar;

    // Return new instance of this fragment and attach arguments to it
    public static PhotoPageFragment newInstance(Uri uri) {

        // Create new empty Bundle object for fragment arguments
        Bundle args = new Bundle();

        // Put photo webpage URL into Bundle object
        args.putParcelable(ARG_URI, uri);

        // Create new instance of this fragment
        PhotoPageFragment fragment = new PhotoPageFragment();

        // Attach arguments to fragment
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get photo webpage URL from the fragment's arguments
        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);

        // Get reference to ProgressBar
        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_page_progress_bar);

        // Set maximum value for ProgressBar
        mProgressBar.setMax(PROGRESS_BAR_MAX_VALUE);

        // Get reference to WebView for photo webpage
        mWebView = (WebView) v.findViewById(R.id.fragment_photo_page_web_view);

        // Enable JavaScript (Flickr webpages require JavaScript)
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Set new WebChromeClient for updating ProgressBar value during webpage download
        mWebView.setWebChromeClient(new WebChromeClient() {

            // Method is called when webpage download progress is changed
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                // If new progress equals to maximum (webpage download complete)
                if (newProgress == PROGRESS_BAR_MAX_VALUE) {

                    // Hide progress bar
                    mProgressBar.setVisibility(View.GONE);

                } else {
                    // Otherwise (webpage is still downloading)

                    // Display progress bar
                    mProgressBar.setVisibility(View.VISIBLE);

                    // And set new progress bar value
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        // Set new WebViewClient and override shouldOverrideUrlLoading
        // (this tells the WebView to load new URLs (for example link clicks) in itself
        // instead of the default browser)
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        // Load photo webpage in WebView
        mWebView.loadUrl(mUri.toString());

        return v;
    }
}
