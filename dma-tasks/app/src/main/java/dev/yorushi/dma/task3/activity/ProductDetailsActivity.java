package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** T4.5: custom scheme and verified App Link to a product page. */
public final class ProductDetailsActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "T4.5";
    }

    @Override
    protected int description() {
        return R.string.desc_product;
    }
}
