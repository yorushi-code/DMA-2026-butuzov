package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P25: verified App Link of the marketplace catalogue. */
public final class MarketplaceActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P25";
    }

    @Override
    protected int description() {
        return R.string.desc_marketplace;
    }
}
