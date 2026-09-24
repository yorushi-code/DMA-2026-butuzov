package dev.yorushi.dma.task3.activity;

import dev.yorushi.dma.R;

/** T4.3: opens PDF documents only. */
public final class PdfViewerActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "T4.3";
    }

    @Override
    protected int description() {
        return R.string.desc_pdf;
    }
}
