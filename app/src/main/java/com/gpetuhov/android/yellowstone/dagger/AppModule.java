package com.gpetuhov.android.yellowstone.dagger;

import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.gpetuhov.android.yellowstone.PhotoFetcher;
import com.gpetuhov.android.yellowstone.QuakeFetcher;
import com.gpetuhov.android.yellowstone.utils.UtilsMap;
import com.gpetuhov.android.yellowstone.utils.UtilsPrefs;
import com.gpetuhov.android.yellowstone.utils.UtilsQuakeList;

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

    // Returns content resolver for the application context
    @Provides
    @Singleton
    ContentResolver providesContentResolver(Application application) {
        return application.getContentResolver();
    }

    // Returns instance of UtilsPrefs
    @Provides
    @Singleton
    UtilsPrefs providesUtilsPrefs(SharedPreferences sharedPreferences) {
        UtilsPrefs utilsPrefs = new UtilsPrefs(sharedPreferences);
        return utilsPrefs;
    }

    // Returns instance of UtilsQuakeList
    @Provides
    @Singleton
    UtilsQuakeList providesUtilsQuakeList(ContentResolver contentResolver) {
        UtilsQuakeList utilsQuakeList = new UtilsQuakeList(contentResolver);
        return utilsQuakeList;
    }

    // Returns instance of UtilsMap
    @Provides
    @Singleton
    UtilsMap providesUtilsMap(Application application, UtilsQuakeList utilsQuakeList) {
        UtilsMap utilsMap = new UtilsMap(application, utilsQuakeList);
        return  utilsMap;
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

    // Returns instance of QuakeFetcher
    @Provides
    @Singleton
    QuakeFetcher providesQuakeFetcher(Retrofit retrofit, ContentResolver contentResolver, UtilsPrefs utilsPrefs) {
        QuakeFetcher quakeFetcher = new QuakeFetcher(retrofit, contentResolver, utilsPrefs);
        return quakeFetcher;
    }

    // Returns instance of PhotoFetcher
    @Provides
    @Singleton
    PhotoFetcher providesPhotoFetcher(OkHttpClient okHttpClient) {
        PhotoFetcher photoFetcher = new PhotoFetcher(okHttpClient);
        return photoFetcher;
    }
}
