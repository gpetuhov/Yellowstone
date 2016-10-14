package com.gpetuhov.android.yellowstone.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


// Service that returns an IBinder for the sync adapter class.
// This Service is used to get access to the SyncAdapter.
public class YellowstoneSyncService extends Service {

    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    // Storage for an instance of the sync adapter
    private static YellowstoneSyncAdapter sYellowstoneSyncAdapter = null;

    @Override
    public void onCreate() {

        // Create the sync adapter as a singleton.
        // Set the sync adapter as syncable.
        // Disallow parallel syncs.
        synchronized (sSyncAdapterLock) {
            if (sYellowstoneSyncAdapter == null) {
                sYellowstoneSyncAdapter = new YellowstoneSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    // Return an object that allows the system to invoke the sync adapter
    @Override
    public IBinder onBind(Intent intent) {

        // Get the object that allows external processes to call onPerformSync().
        // The object is created in the base class code when the SyncAdapter
        // constructors call super().
        return sYellowstoneSyncAdapter.getSyncAdapterBinder();
    }
}