package dev.yorushi.dma.task3.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/** T5.3: bound API; the manifest requires the signature permission BIND_PROTECTED_API. */
public final class ProtectedApiService extends Service {

    private final IBinder binder = new Binder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
