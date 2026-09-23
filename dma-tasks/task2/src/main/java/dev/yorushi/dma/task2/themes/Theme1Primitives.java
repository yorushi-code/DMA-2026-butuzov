package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.util.Locale;

/** Theme 1 — basic syntax, primitive types and operators. */
public final class Theme1Primitives {

    private Theme1Primitives() {
    }

    // ---- T1.1 --------------------------------------------------------------

    /** T1.1: converts a size in dp to physical pixels for the given density factor. */
    public static int dpToPx(float dp, float density) {
        if (density <= 0f) {
            throw new IllegalArgumentException("density must be positive: " + density);
        }
        return Math.round(dp * density);
    }

    // ---- T1.2 --------------------------------------------------------------

    /** T1.2: whole hours, minutes and seconds contained in a millisecond duration. */
    public record TimeBreakdown(long totalHours, long totalMinutes, long totalSeconds) {

        /** Clock-style rendering, e.g. {@code 02:05:09}. */
        public String hms() {
            return String.format(Locale.ROOT, "%02d:%02d:%02d",
                    totalHours, totalMinutes % 60, totalSeconds % 60);
        }
    }

    public static TimeBreakdown breakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("duration cannot be negative: " + millis);
        }
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return new TimeBreakdown(hours, minutes, seconds);
    }

    // ---- T1.3 --------------------------------------------------------------

    /** T1.3: estimated runtime in hours for a battery drained by the radio and the display. */
    public static double batteryLifeHours(int capacityMah, double radioMa, double displayMa) {
        double drain = radioMa + displayMa;
        if (capacityMah <= 0 || drain <= 0) {
            throw new IllegalArgumentException("capacity and drain must be positive");
        }
        return capacityMah / drain;
    }

    // ---- T1.4 --------------------------------------------------------------

    /**
     * T1.4: latitude in [-90, 90] and longitude in [-180, 180]. The negated
     * {@code ||} form also rejects NaN, which fails every comparison.
     */
    public static boolean isValidCoordinate(double latitude, double longitude) {
        boolean latitudeOutOfRange = !(latitude >= -90.0) || latitude > 90.0;
        boolean longitudeOutOfRange = !(longitude >= -180.0) || longitude > 180.0;
        return !latitudeOutOfRange && !longitudeOutOfRange;
    }

    // ---- T1.5 --------------------------------------------------------------

    /** T1.5: permission set packed into an int with bitwise operators. */
    public static final class PermissionFlags {
        public static final int CAMERA = 1;
        public static final int LOCATION = 1 << 1;
        public static final int STORAGE = 1 << 2;

        private int mask;

        public PermissionFlags grant(int permission) {
            mask |= permission;
            return this;
        }

        public PermissionFlags revoke(int permission) {
            mask &= ~permission;
            return this;
        }

        public boolean has(int permission) {
            return (mask & permission) == permission;
        }

        public int mask() {
            return mask;
        }
    }

    public static void demo(Report out) {
        out.section("Theme 1. Primitives and operators");
        out.item("T1.1", "48 dp at xhdpi (2.0) = " + dpToPx(48, 2.0f) + " px, at xxhdpi (3.0) = "
                + dpToPx(48, 3.0f) + " px");
        TimeBreakdown t = breakdown(7_509_000L);
        out.item("T1.2", "7 509 000 ms = " + t.totalHours() + " h / " + t.totalMinutes() + " min / "
                + t.totalSeconds() + " s -> " + t.hms());
        out.item("T1.3", String.format(Locale.ROOT,
                "5000 mAh, radio 120 mA + display 280 mA -> %.1f h", batteryLifeHours(5000, 120, 280)));
        out.item("T1.4", "(54.01, 38.29) valid=" + isValidCoordinate(54.01, 38.29)
                + ", (91.0, 10.0) valid=" + isValidCoordinate(91.0, 10.0));
        PermissionFlags flags = new PermissionFlags()
                .grant(PermissionFlags.CAMERA).grant(PermissionFlags.STORAGE).revoke(PermissionFlags.CAMERA);
        out.item("T1.5", "grant CAMERA+STORAGE, revoke CAMERA -> mask=" + flags.mask()
                + ", STORAGE=" + flags.has(PermissionFlags.STORAGE) + ", CAMERA=" + flags.has(PermissionFlags.CAMERA));
    }
}
