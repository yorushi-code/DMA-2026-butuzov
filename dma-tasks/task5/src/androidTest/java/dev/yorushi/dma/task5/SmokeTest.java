package dev.yorushi.dma.task5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Every item opens and reaches RESUMED. Each screen is photographed, and the depth and
 * size of its view tree are written to files/tour/trees.tsv for the write-up (topic 7).
 */
@RunWith(AndroidJUnit4.class)
public class SmokeTest {

    @Test
    public void everyItemOpens() throws IOException {
        Screens.wake();
        Context context = ApplicationProvider.getApplicationContext();
        StringBuilder trees = new StringBuilder("index\tlabel\tlayout\tdepth\tviews\n");
        for (int i = 0; i < Catalog.ENTRIES.size(); i++) {
            Catalog.Entry entry = Catalog.ENTRIES.get(i);
            try (ActivityScenario<ScreenActivity> scenario = ActivityScenario.launch(ScreenActivity.intent(context, i))) {
                assertEquals(entry.label, Lifecycle.State.RESUMED, Screens.settle(scenario));
                int index = i;
                scenario.onActivity(a -> {
                    assertNotNull(entry.label, Screens.root(a));
                    trees.append(String.format(Locale.ROOT, "%d\t%s\t%s\t%d\t%d%n", index, entry.label,
                            a.getResources().getResourceEntryName(entry.layout),
                            Screens.depth(Screens.root(a)), Screens.count(Screens.root(a))));
                });
                Shots.take(String.format(Locale.ROOT, "%02d_%s", i, context.getResources().getResourceEntryName(entry.layout)));
            }
        }
        // 6 topics x 5 exercises (6.5 has two screens) + 50 mixed items.
        assertEquals(81, Catalog.ENTRIES.size());
        File dir = new File(context.getFilesDir(), "tour");
        if (dir.isDirectory() || dir.mkdirs()) {
            try (FileWriter out = new FileWriter(new File(dir, "trees.tsv"))) {
                out.write(trees.toString());
            }
        }
    }

    @Test
    public void menuOpens() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
            Shots.take("menu");
        }
    }
}
