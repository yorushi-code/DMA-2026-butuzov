package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntPredicate;

/** Theme 2 — control flow and branching. */
public final class Theme2ControlFlow {

    private Theme2ControlFlow() {
    }

    // ---- T2.1 --------------------------------------------------------------

    /** T2.1: {@code PORTRAIT}, {@code LANDSCAPE} or {@code SQUARE} for a window size. */
    public static String orientation(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("window size must be positive");
        }
        if (width == height) {
            return "SQUARE";
        }
        return width > height ? "LANDSCAPE" : "PORTRAIT";
    }

    // ---- T2.2 --------------------------------------------------------------

    /** T2.2: response category of an HTTP status code. */
    public static String httpCategory(int statusCode) {
        return switch (statusCode / 100) {
            case 1 -> "Informational";
            case 2 -> "Success";
            case 3 -> "Redirection";
            case 4 -> "Client error";
            case 5 -> "Server error";
            default -> throw new IllegalArgumentException("not an HTTP status: " + statusCode);
        };
    }

    // ---- T2.3 --------------------------------------------------------------

    /**
     * T2.3: exponential backoff over at most five connection attempts.
     *
     * @param connect returns {@code true} when the attempt with the given 1-based number succeeds
     * @return delays in milliseconds waited before each retry; empty if the first attempt succeeded
     */
    public static List<Long> backoffDelays(IntPredicate connect) {
        final int maxAttempts = 5;
        List<Long> waited = new ArrayList<>();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            if (connect.test(attempt)) {
                return waited;
            }
            waited.add(1000L << (attempt - 1));
        }
        return waited;
    }

    // ---- T2.4 --------------------------------------------------------------

    /** T2.4: skips damaged ids ({@code -1}) and stops at the end-of-session marker ({@code 0}). */
    public static List<Integer> readMessageIds(int[] ids) {
        List<Integer> accepted = new ArrayList<>();
        for (int id : ids) {
            if (id == -1) {
                continue;
            }
            if (id == 0) {
                break;
            }
            accepted.add(id);
        }
        return accepted;
    }

    // ---- T2.5 --------------------------------------------------------------

    /** Outcome of a PIN entry session. */
    public record PinResult(boolean unlocked, int attemptsUsed) {
    }

    /** T2.5: checks a 4-digit PIN with a do-while loop, allowing at most three attempts. */
    public static PinResult enterPin(String correctPin, Iterator<String> attempts) {
        if (correctPin == null || !correctPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly four digits");
        }
        final int maxAttempts = 3;
        int used = 0;
        boolean unlocked;
        do {
            String entered = attempts.hasNext() ? attempts.next() : null;
            used++;
            unlocked = correctPin.equals(entered);
        } while (!unlocked && used < maxAttempts && attempts.hasNext());
        return new PinResult(unlocked, used);
    }

    public static void demo(Report out) {
        out.section("Theme 2. Control flow");
        out.item("T2.1", "1080x2400 -> " + orientation(1080, 2400) + ", 2400x1080 -> "
                + orientation(2400, 1080) + ", 800x800 -> " + orientation(800, 800));
        out.item("T2.2", "200 -> " + httpCategory(200) + ", 304 -> " + httpCategory(304)
                + ", 404 -> " + httpCategory(404) + ", 503 -> " + httpCategory(503));
        out.item("T2.3", "server never answers -> waited " + backoffDelays(attempt -> false) + " ms; "
                + "answers on attempt 3 -> waited " + backoffDelays(attempt -> attempt == 3) + " ms");
        out.item("T2.4", "ids [5, 7, -1, 9, 0, 11] -> accepted " + readMessageIds(new int[] {5, 7, -1, 9, 0, 11}));
        PinResult ok = enterPin("4821", Arrays.asList("1111", "4821").iterator());
        PinResult locked = enterPin("4821", Arrays.asList("1", "2", "3", "4821").iterator());
        out.item("T2.5", "right on 2nd try -> unlocked=" + ok.unlocked() + " after " + ok.attemptsUsed()
                + "; three misses -> unlocked=" + locked.unlocked() + " after " + locked.attemptsUsed());
    }
}
