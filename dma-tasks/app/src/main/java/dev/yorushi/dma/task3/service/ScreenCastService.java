package dev.yorushi.dma.task3.service;

import android.content.pm.ServiceInfo;
import dev.yorushi.dma.R;

/** P34: casts the screen to a TV as a mediaProjection foreground service. */
public final class ScreenCastService extends TypedForegroundService {

    @Override
    protected int foregroundType() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION;
    }

    @Override
    protected int notificationText() {
        return R.string.fgs_cast;
    }

    @Override
    protected int notificationId() {
        return 105;
    }
}
