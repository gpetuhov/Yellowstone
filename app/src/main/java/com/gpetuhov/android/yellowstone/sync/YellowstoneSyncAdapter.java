package com.gpetuhov.android.yellowstone.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.gpetuhov.android.yellowstone.QuakeFetcher;
import com.gpetuhov.android.yellowstone.R;


// SyncAdapter handles the transfer of data between a server and the app
public class YellowstoneSyncAdapter extends AbstractThreadedSyncAdapter {

    public YellowstoneSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    // Performs data transfer. The entire sync adapter runs in a background thread.
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // Create new QuakeFetcher object and fetch data
        new QuakeFetcher().fetchQuakes(getContext());
    }


    // Immediately sync the sync adapter
    public static void syncImmediately(Context context) {

        // Create Bundle and put extras, passed to the SyncAdapter, into it
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        // Start an asynchronous sync operation
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    // Get the fake account to be used with SyncAdapter, or make a new one
    // if the fake account doesn't exist yet.
    public static Account getSyncAccount(Context context) {

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            // Add the account and account type, no password or user data.
            // If not successful, return null.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
        }

        // If new account existed or was successfully created, return the Account object.
        return newAccount;
    }
}