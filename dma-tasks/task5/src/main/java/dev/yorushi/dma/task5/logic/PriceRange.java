package dev.yorushi.dma.task5.logic;

/** Item 12: turns what was typed into "from" and "to" into a valid slider range. */
public final class PriceRange {

    private PriceRange() {
    }

    /**
     * Parses both bounds (empty or garbage falls back to the slider's ends), clamps them
     * into [min, max], snaps them to the slider's step and puts them in order.
     */
    public static int[] normalize(String from, String to, int min, int max, int step) {
        int a = snap(clamp(parse(from, min), min, max), min, step);
        int b = snap(clamp(parse(to, max), min, max), min, step);
        return a <= b ? new int[] {a, b} : new int[] {b, a};
    }

    static int parse(String text, int fallback) {
        String digits = text == null ? "" : text.replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 9) {
            return fallback;
        }
        return Integer.parseInt(digits);
    }

    static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    static int snap(int value, int min, int step) {
        return min + Math.round((value - min) / (float) step) * step;
    }
}
