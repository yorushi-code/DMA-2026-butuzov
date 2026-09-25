package dev.yorushi.dma.task4;

import android.graphics.Bitmap;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Saves a screenshot of the device into the app's files/tour directory, from
 * where docs/task4 takes them (adb exec-out run-as ... cat). Tests pass or fail
 * on their assertions; the screenshots are a by-product.
 */
final class Shots {

    private Shots() {
    }

    static void take(String name) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        try {
            // Let the last frame (ripples, error popups) settle before capturing.
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        if (bitmap == null) {
            return;
        }
        File dir = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getFilesDir(), "tour");
        if (!dir.isDirectory() && !dir.mkdirs()) {
            return;
        }
        try (FileOutputStream out = new FileOutputStream(new File(dir, name + ".png"))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException ignored) {
            // A missing screenshot must not fail the functional test.
        }
    }
}
