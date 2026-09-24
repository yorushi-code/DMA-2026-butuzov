package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** T4.1: receives images shared through ACTION_SEND. */
public final class ShareTargetActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "T4.1";
    }

    @Override
    protected int description() {
        return R.string.desc_share;
    }
}
