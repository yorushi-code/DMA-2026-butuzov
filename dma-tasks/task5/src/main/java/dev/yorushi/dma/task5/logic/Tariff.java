package dev.yorushi.dma.task5.logic;

/** Item 8: monthly fee of the configurable plan. */
public final class Tariff {

    public static final int BASE = 200;
    /** Roubles per 100 minutes. */
    public static final int PER_100_MINUTES = 30;
    public static final int PER_GB = 12;

    private Tariff() {
    }

    public static int monthly(int minutes, int gigabytes) {
        return BASE + minutes / 100 * PER_100_MINUTES + gigabytes * PER_GB;
    }
}
