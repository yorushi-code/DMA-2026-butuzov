package dev.yorushi.dma.task3.service;

import android.content.pm.ServiceInfo;
import dev.yorushi.dma.R;

/** P31: records voice notes as a microphone foreground service. */
public final class VoiceNoteService extends TypedForegroundService {

    @Override
    protected int foregroundType() {
        return ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
    }

    @Override
    protected int notificationText() {
        return R.string.fgs_voice;
    }

    @Override
    protected int notificationId() {
        return 102;
    }
}
