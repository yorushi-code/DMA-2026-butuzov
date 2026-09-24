package dev.yorushi.dma.task3.service;

import android.content.pm.ServiceInfo;
import dev.yorushi.dma.R;

/** P32: shares the courier location as a location foreground service. */
public final class CourierLocationService extends TypedForegroundService {

    @Override
    protected int foregroundType() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
    }

    @Override
    protected int notificationText() {
        return R.string.fgs_location;
    }

    @Override
    protected int notificationId() {
        return 103;
    }
}
