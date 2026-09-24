package dev.yorushi.dma.task3.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * P11: runs in an isolated process that has no permissions and cannot read the
 * app's files, so untrusted or dynamically loaded rendering code is contained.
 */
public final class IsolatedRendererService extends Service {

    private final IBinder binder = new Binder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
