package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** T4.2: handles tel: links. */
public final class DialerActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "T4.2";
    }

    @Override
    protected int description() {
        return R.string.desc_dialer;
    }
}
