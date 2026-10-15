package dev.yorushi.dma.task5;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Topic 7: the window background is dropped exactly where the layout's root paints the
 * whole screen itself, and kept everywhere else. Run with
 * "adb shell setprop debug.hwui.overdraw show" the screenshots become the GPU overdraw
 * pictures of docs/task5.
 */
@RunWith(AndroidJUnit4.class)
public class OverdrawTest {

    private static final int[] OPAQUE_ROOTS = {
        R.layout.m33_reels, R.layout.m39_stories, R.layout.m44_qr, R.layout.m50_product,
    };

    private static final int[] REGULAR = {
        R.layout.t1_1_pin_row, R.layout.t6_5_cart, R.layout.m38_gallery, R.layout.m42_cart_bar,
    };

    @Test
    public void windowBackgroundOnlyWhereItIsVisible() {
        for (int layout : OPAQUE_ROOTS) {
            try (ActivityScenario<ScreenActivity> s = Screens.open(layout)) {
                s.onActivity(a -> assertNull(a.getString(a.entry().title), a.getWindow().getDecorView().getBackground()));
                Shots.take("od_" + Screens.indexOf(layout));
            }
        }
        for (int layout : REGULAR) {
            try (ActivityScenario<ScreenActivity> s = Screens.open(layout)) {
                s.onActivity(a -> assertNotNull(a.getString(a.entry().title), a.getWindow().getDecorView().getBackground()));
                Shots.take("od_" + Screens.indexOf(layout));
            }
        }
    }
}
