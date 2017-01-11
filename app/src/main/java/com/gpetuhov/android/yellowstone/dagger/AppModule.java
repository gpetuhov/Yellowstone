package com.gpetuhov.android.yellowstone.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

// Dagger module tells, what instances will be instantiated
@Module
public class AppModule {

    // USGS base URL
    public static final String USGS_BASE_URL = "http://earthquake.usgs.gov/fdsnws/event/1/";

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    // Returns instance of Application class
    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    // Returns instance of default SharedPreferences.
    // This instance will be instantiated only once and will exist during entire application lifecycle.
    @Provides
    @Singleton
    SharedPreferences providesDefaultSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    // Returns instance of OkHttpClient
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        return client;
    }

    // Returns instance of Retrofit for fetching quakes
    @Provides
    @Singleton
    Retrofit provideRetrofitForQuakes(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(USGS_BASE_URL)
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}
