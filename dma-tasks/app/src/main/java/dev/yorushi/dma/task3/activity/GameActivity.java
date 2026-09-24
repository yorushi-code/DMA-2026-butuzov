package dev.yorushi.dma.task3.activity;

import android.content.res.Configuration;
import dev.yorushi.dma.R;

/** T3.3: rotations are delivered here instead of recreating the activity. */
public final class GameActivity extends IntentEchoActivity {

    private int handledChanges;

    @Override
    protected String itemId() {
        return "T3.3";
    }

    @Override
    protected int description() {
        return R.string.desc_game;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handledChanges++;
        onNewIntent(getIntent());
    }

    @Override
    protected String extraDetails() {
        String orientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                ? "landscape" : "portrait";
        return "\norientation: " + orientation + "\nconfiguration changes handled without recreation: "
                + handledChanges;
    }
}
