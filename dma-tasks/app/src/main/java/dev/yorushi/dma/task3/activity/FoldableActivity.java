package dev.yorushi.dma.task3.activity;

import android.graphics.Rect;
import android.os.Build;
import java.util.Locale;
import dev.yorushi.dma.R;

/** P41: letterboxed between the min and max aspect ratios from the manifest. */
public final class FoldableActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P41";
    }

    @Override
    protected int description() {
        return R.string.desc_foldable;
    }

    @Override
    protected String extraDetails() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return "";
        }
        Rect bounds = getWindowManager().getCurrentWindowMetrics().getBounds();
        float ratio = (float) Math.max(bounds.width(), bounds.height()) / Math.min(bounds.width(), bounds.height());
        return String.format(Locale.ROOT, "\nwindow: %d x %d px, aspect ratio %.2f:1 (allowed 1.33..2.40)",
                bounds.width(), bounds.height(), ratio);
    }
}
