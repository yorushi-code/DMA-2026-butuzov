package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P21: started by TECH_DISCOVERED for Mifare cards. */
public final class NfcReaderActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P21";
    }

    @Override
    protected int description() {
        return R.string.desc_nfc;
    }
}
