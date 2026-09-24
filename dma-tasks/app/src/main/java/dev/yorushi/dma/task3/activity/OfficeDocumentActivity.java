package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** P23: opens .docx and .xlsx attachments. */
public final class OfficeDocumentActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P23";
    }

    @Override
    protected int description() {
        return R.string.desc_office;
    }
}
