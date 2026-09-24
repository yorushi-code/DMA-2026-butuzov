package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P17: startable from outside only by holders of BIND_ACCESSIBILITY_SERVICE. */
public final class AccessibilityGatedActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P17";
    }

    @Override
    protected int description() {
        return R.string.desc_accessibility_gated;
    }
}
