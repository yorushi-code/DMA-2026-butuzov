package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P02: reachable through a partner action only; has no launcher entry. */
public final class HiddenEntryActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P02";
    }

    @Override
    protected int description() {
        return R.string.desc_hidden_entry;
    }
}
