package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P49: started when a supported USB device is attached. */
public final class UsbDeviceActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P49";
    }

    @Override
    protected int description() {
        return R.string.desc_usb;
    }
}
