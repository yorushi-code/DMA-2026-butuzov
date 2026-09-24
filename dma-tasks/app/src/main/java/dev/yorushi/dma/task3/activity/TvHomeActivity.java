package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P44: Android TV home screen entry. */
public final class TvHomeActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P44";
    }

    @Override
    protected int description() {
        return R.string.desc_tv;
    }
}
