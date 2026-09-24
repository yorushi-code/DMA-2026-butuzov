package dev.yorushi.dma.task3.service;

import android.content.pm.ServiceInfo;
import dev.yorushi.dma.R;

/** P07: mediaPlayback foreground service running in its own :playback_process. */
public final class PlaybackService extends TypedForegroundService {

    @Override
    protected int foregroundType() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
    }

    @Override
    protected int notificationText() {
        return R.string.fgs_playback;
    }

    @Override
    protected int notificationId() {
        return 101;
    }
}
