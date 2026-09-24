package dev.yorushi.dma.task3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/** P26: headset button presses (MEDIA_BUTTON). */
public final class MediaButtonReceiver extends BroadcastReceiver {

    @Override
    @SuppressWarnings("deprecation")
    public void onReceive(Context context, Intent intent) {
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.i("MediaButtonReceiver", "media key " + KeyEvent.keyCodeToString(event.getKeyCode()));
        }
    }
}
