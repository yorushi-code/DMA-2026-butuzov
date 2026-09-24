package dev.yorushi.dma;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji2.text.EmojiCompatInitializer;
import androidx.startup.AppInitializer;
import androidx.work.Configuration;
import dev.yorushi.dma.task3.Notifications;

/**
 * Application class registered in the manifest (task 3, T2.3). It performs the
 * initialisation that the manifest moved away from androidx.startup.
 */
public final class MainApplication extends Application implements Configuration.Provider {

    private static final String TAG = "MainApplication";
    private static final String META_MAPKIT_KEY = "dev.yorushi.dma.YANDEX_MAPKIT_KEY";
    private static final String META_REFRESH_RATE = "dev.yorushi.dma.PREFERRED_REFRESH_RATE";

    private String mapKitApiKey;

    @Override
    public void onCreate() {
        super.onCreate();
        // T7.4: EmojiCompat's startup initializer is removed from the manifest,
        // so it is started here explicitly through the same androidx.startup API.
        AppInitializer.getInstance(this).initializeComponent(EmojiCompatInitializer.class);

        Notifications.createChannel(this);

        Bundle meta = applicationMetaData();
        // T7.1: MapKit receives its key in code; the manifest only stores it.
        mapKitApiKey = meta.getString(META_MAPKIT_KEY);
        // T7.5: the refresh rate requested in the manifest is applied to every window.
        int refreshRate = meta.getInt(META_REFRESH_RATE, 0);
        if (refreshRate > 0) {
            registerActivityLifecycleCallbacks(new RefreshRateApplier(refreshRate));
        }
    }

    /** P35: WorkManager is created lazily from this configuration on first use. */
    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build();
    }

    public String mapKitApiKey() {
        return mapKitApiKey;
    }

    private Bundle applicationMetaData() {
        try {
            ApplicationInfo info = getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData != null ? info.metaData : Bundle.EMPTY;
        } catch (PackageManager.NameNotFoundException impossible) {
            throw new IllegalStateException("own package not found", impossible);
        }
    }

    /** Picks the display mode closest to the requested refresh rate at the current resolution. */
    private static final class RefreshRateApplier implements ActivityLifecycleCallbacks {
        private final float targetHz;

        RefreshRateApplier(float targetHz) {
            this.targetHz = targetHz;
        }

        @SuppressWarnings("deprecation") // Activity.getDisplay() exists only from API 30
        private static Display displayOf(Activity activity) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                    ? activity.getDisplay()
                    : activity.getWindowManager().getDefaultDisplay();
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            Display display = displayOf(activity);
            if (display == null) {
                return;
            }
            Display.Mode current = display.getMode();
            Display.Mode best = current;
            for (Display.Mode mode : display.getSupportedModes()) {
                boolean sameResolution = mode.getPhysicalWidth() == current.getPhysicalWidth()
                        && mode.getPhysicalHeight() == current.getPhysicalHeight();
                if (sameResolution && Math.abs(mode.getRefreshRate() - targetHz)
                        < Math.abs(best.getRefreshRate() - targetHz)) {
                    best = mode;
                }
            }
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.preferredDisplayModeId = best.getModeId();
            activity.getWindow().setAttributes(params);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    }
}
