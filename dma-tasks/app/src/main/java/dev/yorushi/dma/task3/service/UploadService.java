package dev.yorushi.dma.task3.service;

import android.content.pm.ServiceInfo;
import dev.yorushi.dma.R;

/**
 * P33: large uploads as a dataSync foreground service. Android 15 caps dataSync
 * at 6 hours in 24 and then calls onTimeout; the service must stop within a few
 * seconds or the system crashes the app.
 */
public final class UploadService extends TypedForegroundService {

    @Override
    protected int foregroundType() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
    }

    @Override
    protected int notificationText() {
        return R.string.fgs_upload;
    }

    @Override
    protected int notificationId() {
        return 104;
    }

    @Override
    public void onTimeout(int startId, int fgsType) {
        stopSelf(startId);
    }
}
