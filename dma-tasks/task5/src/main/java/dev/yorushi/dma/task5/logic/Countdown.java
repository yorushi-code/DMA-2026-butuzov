package dev.yorushi.dma.task5.logic;

import java.util.Locale;

/** Items 37 and 47: m:ss for timers. */
public final class Countdown {

    private Countdown() {
    }

    public static String format(long seconds) {
        long s = Math.max(0, seconds);
        return String.format(Locale.ROOT, "%d:%02d", s / 60, s % 60);
    }

    /**
     * Seconds to show for the milliseconds left on a countdown: rounded up, so the
     * display only reaches 0:00 when the time is really over.
     */
    public static long secondsLeft(long millisLeft) {
        return millisLeft <= 0 ? 0 : (millisLeft + 999) / 1000;
    }
}
