package dev.yorushi.dma.task3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.StringRes;
import androidx.core.app.ServiceCompat;
import dev.yorushi.dma.task3.Notifications;

/**
 * Foreground service started with the type declared for it in the manifest.
 * Since Android 14 the type passed to startForeground must be one the manifest
 * declares, and the matching FOREGROUND_SERVICE_* permission must be held.
 */
abstract class TypedForegroundService extends Service {

    /** One of the {@code ServiceInfo.FOREGROUND_SERVICE_TYPE_*} constants. */
    protected abstract int foregroundType();

    @StringRes
    protected abstract int notificationText();

    protected abstract int notificationId();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceCompat.startForeground(this, notificationId(),
                Notifications.ongoing(this, notificationText()), foregroundType());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
