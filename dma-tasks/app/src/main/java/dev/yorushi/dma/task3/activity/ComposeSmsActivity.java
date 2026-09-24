package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P28: composes an SMS from smsto: links. */
public final class ComposeSmsActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P28";
    }

    @Override
    protected int description() {
        return R.string.desc_sms;
    }
}
