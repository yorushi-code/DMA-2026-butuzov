package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/** Theme 3 — arrays, strings and formatting. */
public final class Theme3ArraysStrings {

    private Theme3ArraysStrings() {
    }

    // ---- T3.1 --------------------------------------------------------------

    /** T3.1: trims, lower-cases and collapses runs of whitespace in a search query. */
    public static String normalizeQuery(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    // ---- T3.2 --------------------------------------------------------------

    /** T3.2: reverses animation frames in place, without a second array. */
    public static void reverseInPlace(String[] frames) {
        for (int left = 0, right = frames.length - 1; left < right; left++, right--) {
            String swap = frames[left];
            frames[left] = frames[right];
            frames[right] = swap;
        }
    }

    // ---- T3.3 --------------------------------------------------------------

    /** T3.3: masks a 16-digit card number as {@code **** **** **** 1234}. */
    public static String maskCardNumber(String cardNumber) {
        String digits = cardNumber == null ? "" : cardNumber.replaceAll("[\\s-]", "");
        if (!digits.matches("\\d{16}")) {
            throw new IllegalArgumentException("card number must contain exactly 16 digits");
        }
        return "**** **** **** " + digits.substring(12);
    }

    // ---- T3.4 --------------------------------------------------------------

    /** T3.4: largest absolute jump between neighbouring accelerometer samples. */
    public static double maxSpike(double[] samples) {
        if (samples.length < 2) {
            throw new IllegalArgumentException("need at least two samples");
        }
        double max = 0;
        for (int i = 1; i < samples.length; i++) {
            max = Math.max(max, Math.abs(samples[i] - samples[i - 1]));
        }
        return max;
    }

    // ---- T3.5 --------------------------------------------------------------

    /** T3.5: builds {@code ?key1=val1&key2=val2} with URL-encoded keys and values. */
    public static String buildQuery(String[] keys, String[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("keys and values must have the same length");
        }
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            query.append(i == 0 ? '?' : '&')
                    .append(encode(keys[i]))
                    .append('=')
                    .append(encode(values[i]));
        }
        return query.toString();
    }

    private static String encode(String value) {
        // The Charset overload is Java 10+ and unavailable below Android 13.
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException impossible) {
            throw new AssertionError("UTF-8 is always supported", impossible);
        }
    }

    public static void demo(Report out) {
        out.section("Theme 3. Arrays, strings and formatting");
        out.item("T3.1", "\"  Wireless   HEADPHONES  pro \" -> \""
                + normalizeQuery("  Wireless   HEADPHONES  pro ") + "\"");
        String[] frames = {"frame_01", "frame_02", "frame_03", "frame_04"};
        reverseInPlace(frames);
        out.item("T3.2", "frames reversed in place -> " + Arrays.toString(frames));
        out.item("T3.3", "4276 3800 1234 5678 -> " + maskCardNumber("4276 3800 1234 5678"));
        double[] samples = new Random(42).doubles(100, -9.8, 9.8).toArray();
        out.item("T3.4", String.format(Locale.ROOT, "max spike across 100 samples = %.3f m/s^2",
                maxSpike(samples)));
        out.item("T3.5", buildQuery(new String[] {"q", "page", "sort"},
                new String[] {"red shoes", "2", "price:asc"}));
    }
}
