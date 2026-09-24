package dev.yorushi.dma.task3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import dev.yorushi.dma.task3.work.SyncWorker;

/**
 * P36: RECEIVE_BOOT_COMPLETED + this receiver + the sync. Android 8+ forbids
 * starting background services from the background, so the receiver hands the
 * work to WorkManager, which then starts DatabaseSyncService.
 */
public final class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.from(SyncWorker.class));
        }
    }
}
