package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P30: opens .mycfg configuration files. */
public final class ConfigFileActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P30";
    }

    @Override
    protected int description() {
        return R.string.desc_config;
    }
}
