package dev.yorushi.dma.task3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import dev.yorushi.dma.R;

/** Notification channel and ongoing notification shared by the foreground services. */
public final class Notifications {

    public static final String CHANNEL_BACKGROUND = "background_work";

    private Notifications() {
    }

    public static void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_BACKGROUND,
                context.getString(R.string.channel_background), NotificationManager.IMPORTANCE_LOW);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    public static Notification ongoing(Context context, @StringRes int text) {
        return new NotificationCompat.Builder(context, CHANNEL_BACKGROUND)
                .setSmallIcon(R.drawable.ic_shortcut)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(text))
                .setOngoing(true)
                .build();
    }
}
