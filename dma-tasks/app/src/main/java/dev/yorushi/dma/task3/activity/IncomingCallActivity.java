package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** T3.4: incoming call screen in its own singleInstance task. */
public final class IncomingCallActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "T3.4";
    }

    @Override
    protected int description() {
        return R.string.desc_incoming_call;
    }
}
