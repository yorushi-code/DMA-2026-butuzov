package dev.yorushi.dma.task3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/** T3.2: local database sync; exported="false" keeps other apps from starting it. */
public final class DatabaseSyncService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DatabaseSyncService", "sync requested");
        stopSelf(startId);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
