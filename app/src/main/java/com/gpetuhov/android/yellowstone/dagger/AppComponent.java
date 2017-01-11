package com.gpetuhov.android.yellowstone.dagger;

import com.gpetuhov.android.yellowstone.MainActivity;
import com.gpetuhov.android.yellowstone.PhotoLoader;
import com.gpetuhov.android.yellowstone.QuakeFragment;
import com.gpetuhov.android.yellowstone.QuakeListFragment;
import com.gpetuhov.android.yellowstone.QuakeMapFragment;
import com.gpetuhov.android.yellowstone.QuakePagerActivity;
import com.gpetuhov.android.yellowstone.QuakePollService;
import com.gpetuhov.android.yellowstone.sync.YellowstoneSyncAdapter;

import javax.inject.Singleton;

import dagger.Component;

// Dagger component tells, into which classes instances instantiated by Module will be injected.
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(YellowstoneSyncAdapter syncAdapter);
    void inject(QuakePollService pollService);
    void inject(QuakeListFragment quakeListFragment);
    void inject(PhotoLoader photoLoader);
    void inject(QuakePagerActivity quakePagerActivity);
    void inject(QuakeFragment quakeFragment);
    void inject(QuakeMapFragment quakeMapFragment);
}
