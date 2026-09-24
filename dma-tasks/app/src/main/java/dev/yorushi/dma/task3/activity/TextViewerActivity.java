package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** T4.4: accepts text/plain and text/html. */
public final class TextViewerActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "T4.4";
    }

    @Override
    protected int description() {
        return R.string.desc_text;
    }
}
