package com.gpetuhov.android.yellowstone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


// A bound Service that instantiates the authenticator when started.
// This Service is used to get access to the authenticator.
public class YellowstoneAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private YellowstoneAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new YellowstoneAuthenticator(this);
    }

    // When the system binds to this Service, return the authenticator's IBinder.
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
