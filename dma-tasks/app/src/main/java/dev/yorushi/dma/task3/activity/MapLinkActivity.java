package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P22: handles geo: locations. */
public final class MapLinkActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P22";
    }

    @Override
    protected int description() {
        return R.string.desc_map;
    }
}
