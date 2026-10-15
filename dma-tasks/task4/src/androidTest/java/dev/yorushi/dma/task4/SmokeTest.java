package dev.yorushi.dma.task4;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Every screen of the menu opens and reaches RESUMED: no crash at startup (mistakes 1 and 6). */
@RunWith(AndroidJUnit4.class)
public class SmokeTest {

    @Test
    public void everyMenuScreenOpens() {
        Context context = ApplicationProvider.getApplicationContext();
        for (Catalog.Entry entry : Catalog.ENTRIES) {
            try (ActivityScenario<?> scenario = ActivityScenario.launch(new Intent(context, entry.activity))) {
                assertEquals(entry.items, Lifecycle.State.RESUMED, scenario.getState());
            }
        }
        assertEquals(37, Catalog.ENTRIES.size());
    }

    @Test
    public void menuOpens() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            assertEquals(Lifecycle.State.RESUMED, scenario.getState());
            Shots.take("00_menu");
        }
    }
}
