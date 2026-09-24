package dev.yorushi.dma.task3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import dev.yorushi.dma.task3.work.SyncWorker;

/** P16: accepts sync requests only from senders holding the signature permission. */
public final class SecureSyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequest.from(SyncWorker.class));
    }
}
