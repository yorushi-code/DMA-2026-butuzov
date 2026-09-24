package dev.yorushi.dma.task3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import dev.yorushi.dma.task3.sync.NotesSyncAdapter;

/** P39: exposes the sync adapter to the system SyncManager. */
public final class SyncAdapterService extends Service {

    private static final Object LOCK = new Object();
    private static NotesSyncAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        // The framework may bind several times; one adapter instance must serve all.
        synchronized (LOCK) {
            if (adapter == null) {
                adapter = new NotesSyncAdapter(getApplicationContext());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return adapter.getSyncAdapterBinder();
    }
}
