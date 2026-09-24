package dev.yorushi.dma.task3.work;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import dev.yorushi.dma.task3.service.DatabaseSyncService;

/** Deferred sync started after a reboot (P36) or a secure sync broadcast (P16). */
public final class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // WorkManager runs while the app is allowed to work in the background,
        // so starting the in-app service here is permitted on every API level.
        getApplicationContext().startService(new Intent(getApplicationContext(), DatabaseSyncService.class));
        return Result.success();
    }
}
