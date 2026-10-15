package dev.yorushi.dma.task5;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.function.Consumer;

/** Opening catalog items by their layout, and measuring view trees. */
final class Screens {

    private Screens() {
    }

    static int indexOf(int layout) {
        for (int i = 0; i < Catalog.ENTRIES.size(); i++) {
            if (Catalog.ENTRIES.get(i).layout == layout) {
                return i;
            }
        }
        throw new IllegalArgumentException("no catalog entry for layout " + layout);
    }

    private static boolean awake;

    /** Screen on and unlocked: a keyguard over the activity would pause it mid-test. */
    static void wake() {
        if (!awake) {
            android.app.UiAutomation ui = InstrumentationRegistry.getInstrumentation().getUiAutomation();
            ui.executeShellCommand("input keyevent KEYCODE_WAKEUP");
            ui.executeShellCommand("wm dismiss-keyguard");
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            awake = true;
        }
    }

    static ActivityScenario<ScreenActivity> open(int layout) {
        wake();
        Context context = ApplicationProvider.getApplicationContext();
        return ActivityScenario.launch(ScreenActivity.intent(context, indexOf(layout)));
    }

    /** Opens the item and runs the check on the main thread once it is laid out. */
    static void check(int layout, Consumer<ScreenActivity> check) {
        try (ActivityScenario<ScreenActivity> scenario = open(layout)) {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
            scenario.onActivity(check::accept);
        }
    }

    /** Levels of ViewGroups from the layout's root down (a lone View has depth 0). */
    static int depth(View view) {
        if (!(view instanceof ViewGroup)) {
            return 0;
        }
        ViewGroup group = (ViewGroup) view;
        int max = 0;
        for (int i = 0; i < group.getChildCount(); i++) {
            max = Math.max(max, depth(group.getChildAt(i)));
        }
        return 1 + max;
    }

    static int count(View view) {
        if (!(view instanceof ViewGroup)) {
            return 1;
        }
        ViewGroup group = (ViewGroup) view;
        int n = 1;
        for (int i = 0; i < group.getChildCount(); i++) {
            n += count(group.getChildAt(i));
        }
        return n;
    }

    static View root(ScreenActivity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    /** Position of a view on screen. */
    static int[] onScreen(View view) {
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        return xy;
    }

    static int dp(View view, float dp) {
        return Math.round(dp * view.getResources().getDisplayMetrics().density);
    }
}
