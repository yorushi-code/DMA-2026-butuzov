package dev.yorushi.dma.task3.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/** P39: sync adapter for the notes provider. */
public final class NotesSyncAdapter extends AbstractThreadedSyncAdapter {

    public NotesSyncAdapter(Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        Log.i("NotesSyncAdapter", "sync " + authority + " for " + account.name);
    }
}
