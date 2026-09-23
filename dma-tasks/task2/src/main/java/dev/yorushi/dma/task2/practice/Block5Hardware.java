package dev.yorushi.dma.task2.practice;

import dev.yorushi.dma.task2.Report;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/** Practical block 5 — hardware, geolocation and background work (items 41–50). */
public final class Block5Hardware {

    /** Mean Earth radius (IUGG) in metres. */
    public static final double EARTH_RADIUS_M = 6_371_008.8;

    private Block5Hardware() {
    }

    // ---- P41 ---------------------------------------------------------------

    /** P41: great-circle distance between two GPS fixes, haversine formula. */
    public static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        return 2 * EARTH_RADIUS_M * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    // ---- P42 ---------------------------------------------------------------

    /** P42: point inside a circular geofence; the boundary counts as inside. */
    public static boolean isInsideGeofence(double lat, double lon, double centerLat, double centerLon,
            double radiusMeters) {
        return haversineMeters(lat, lon, centerLat, centerLon) <= radiusMeters;
    }

    // ---- P43 ---------------------------------------------------------------

    /** P43: GPS polling interval in seconds for the current battery level. */
    public static int gpsIntervalSeconds(int batteryPercent) {
        if (batteryPercent < 0 || batteryPercent > 100) {
            throw new IllegalArgumentException("battery level must be 0..100: " + batteryPercent);
        }
        if (batteryPercent > 50) {
            return 5;
        }
        return batteryPercent >= 15 ? 30 : 5 * 60;
    }

    // ---- P44 ---------------------------------------------------------------

    public enum MotionState { NORMAL, FREE_FALL, IMPACT }

    /**
     * P44: a resting phone reads about 9.8 m/s² (gravity). Near zero total
     * acceleration means the device is falling freely; a spike far above gravity
     * means it hit something.
     */
    public static MotionState detectFall(double x, double y, double z) {
        final double freeFallThreshold = 2.0;
        final double impactThreshold = 25.0;
        double magnitude = Math.sqrt(x * x + y * y + z * z);
        if (magnitude < freeFallThreshold) {
            return MotionState.FREE_FALL;
        }
        return magnitude > impactThreshold ? MotionState.IMPACT : MotionState.NORMAL;
    }

    // ---- P45 ---------------------------------------------------------------

    /**
     * P45: counts steps as local maxima of vertical acceleration above
     * {@code threshold}; plateaus of equal samples count once.
     */
    public static int countSteps(double[] verticalAcceleration, double threshold) {
        int steps = 0;
        for (int i = 1; i < verticalAcceleration.length - 1; i++) {
            double value = verticalAcceleration[i];
            if (value > threshold && value > verticalAcceleration[i - 1] && value >= verticalAcceleration[i + 1]) {
                steps++;
            }
        }
        return steps;
    }

    // ---- P46 ---------------------------------------------------------------

    /**
     * P46: screen brightness for ambient light. Perceived brightness is roughly
     * logarithmic, so {@code log10(lux + 1)} is mapped linearly from darkness to
     * direct sunlight (about 10 000 lx).
     */
    public static int brightnessPercent(double lux) {
        final double sunlightLux = 10_000;
        double scaled = Math.log10(Math.max(0, lux) + 1) / Math.log10(sunlightLux + 1);
        return (int) Math.round(Math.min(1.0, scaled) * 100);
    }

    // ---- P47 ---------------------------------------------------------------

    /** P47: heavy sync may start only on Wi-Fi while charging. */
    public static boolean canStartHeavySync(boolean onWifi, boolean charging) {
        return onWifi && charging;
    }

    // ---- P48 ---------------------------------------------------------------

    public enum Network { MOBILE, WIFI }

    /** P48: traffic per network type; warns once when mobile data reaches the 5 GB limit. */
    public static final class TrafficMonitor {
        public static final long MOBILE_LIMIT_BYTES = 5L * 1024 * 1024 * 1024;

        private final Map<Network, Long> totals = new EnumMap<>(Network.class);
        private final Consumer<Long> onLimitReached;
        private boolean warned;

        public TrafficMonitor(Consumer<Long> onLimitReached) {
            this.onLimitReached = onLimitReached;
            for (Network network : Network.values()) {
                totals.put(network, 0L);
            }
        }

        public void record(Network network, long bytes) {
            long total = totals.get(network) + bytes;
            totals.put(network, total);
            if (network == Network.MOBILE && !warned && total >= MOBILE_LIMIT_BYTES) {
                warned = true;
                onLimitReached.accept(total);
            }
        }

        public long total(Network network) {
            return totals.get(network);
        }
    }

    // ---- P49 ---------------------------------------------------------------

    public enum PlayerState { IDLE, INITIALIZED, PREPARED, PLAYING, PAUSED, STOPPED }

    /** P49: audio player lifecycle modelled on {@code MediaPlayer}; illegal jumps are rejected. */
    public static final class AudioPlayer {
        private static final Map<PlayerState, Set<PlayerState>> ALLOWED = new EnumMap<>(PlayerState.class);

        static {
            ALLOWED.put(PlayerState.IDLE, EnumSet.of(PlayerState.INITIALIZED));
            ALLOWED.put(PlayerState.INITIALIZED, EnumSet.of(PlayerState.PREPARED, PlayerState.IDLE));
            ALLOWED.put(PlayerState.PREPARED, EnumSet.of(PlayerState.PLAYING, PlayerState.STOPPED, PlayerState.IDLE));
            ALLOWED.put(PlayerState.PLAYING, EnumSet.of(PlayerState.PAUSED, PlayerState.STOPPED, PlayerState.IDLE));
            ALLOWED.put(PlayerState.PAUSED, EnumSet.of(PlayerState.PLAYING, PlayerState.STOPPED, PlayerState.IDLE));
            // A stopped MediaPlayer must be prepared again before it can play.
            ALLOWED.put(PlayerState.STOPPED, EnumSet.of(PlayerState.PREPARED, PlayerState.IDLE));
        }

        private PlayerState state = PlayerState.IDLE;
        private final List<PlayerState> history = new ArrayList<>(Collections.singletonList(PlayerState.IDLE));

        public void moveTo(PlayerState next) {
            if (!ALLOWED.get(state).contains(next)) {
                throw new IllegalStateException("illegal transition " + state + " -> " + next);
            }
            state = next;
            history.add(next);
        }

        public boolean canMoveTo(PlayerState next) {
            return ALLOWED.get(state).contains(next);
        }

        public PlayerState state() {
            return state;
        }

        public List<PlayerState> history() {
            return Collections.unmodifiableList(history);
        }
    }

    // ---- P50 ---------------------------------------------------------------

    /** Device facts collected by the platform layer (Build, StatFs) for a crash report. */
    public record DeviceInfo(String androidVersion, int sdkInt, String manufacturer, String model,
            long freeStorageBytes) {
    }

    /** P50: formatted crash report with device context and the full stack trace. */
    public static String formatCrashReport(Throwable crash, DeviceInfo device, Instant when, String threadName) {
        StringWriter trace = new StringWriter();
        crash.printStackTrace(new PrintWriter(trace));
        String time = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(when.atOffset(ZoneOffset.UTC));
        return "===== CRASH REPORT =====\n"
                + "Time:     " + time + "\n"
                + "Thread:   " + threadName + "\n"
                + "Android:  " + device.androidVersion() + " (API " + device.sdkInt() + ")\n"
                + "Device:   " + device.manufacturer() + " " + device.model() + "\n"
                + "Storage:  " + Block3Network.formatBytes(device.freeStorageBytes()) + " free\n"
                + "Error:    " + crash + "\n"
                + "----- stack trace -----\n"
                + trace.toString().trim() + "\n"
                + "========================";
    }

    public static void demo(Report out) {
        out.section("Practice block 5. Hardware, location and background work");
        double courier = haversineMeters(54.1931, 37.6177, 54.2044, 37.6118);
        out.item("P41", String.format(Locale.ROOT, "user (54.1931, 37.6177) to courier (54.2044, 37.6118) = %.0f m",
                courier));
        out.item("P42", "courier within 1500 m of user=" + isInsideGeofence(54.2044, 37.6118, 54.1931, 37.6177, 1500)
                + ", within 1000 m=" + isInsideGeofence(54.2044, 37.6118, 54.1931, 37.6177, 1000));
        out.item("P43", "battery 80% -> " + gpsIntervalSeconds(80) + " s, 30% -> " + gpsIntervalSeconds(30)
                + " s, 10% -> " + gpsIntervalSeconds(10) + " s");
        out.item("P44", "(0, 9.8, 0.3) -> " + detectFall(0, 9.8, 0.3) + ", (0.2, 0.4, 0.1) -> "
                + detectFall(0.2, 0.4, 0.1) + ", (18, 22, 9) -> " + detectFall(18, 22, 9));
        double[] walk = {9.8, 11.9, 12.6, 10.1, 9.6, 12.2, 13.0, 12.8, 9.9, 9.5, 11.8, 12.4, 10.0, 10.4, 10.2};
        out.item("P45", "15 samples, barrier 11.5 m/s^2 -> " + countSteps(walk, 11.5) + " steps");
        out.item("P46", "0 lx -> " + brightnessPercent(0) + "%, 50 lx -> " + brightnessPercent(50)
                + "%, 500 lx -> " + brightnessPercent(500) + "%, 10000 lx -> " + brightnessPercent(10_000) + "%");
        out.item("P47", "Wi-Fi+charging=" + canStartHeavySync(true, true) + ", Wi-Fi on battery="
                + canStartHeavySync(true, false) + ", mobile+charging=" + canStartHeavySync(false, true));
        List<String> warnings = new ArrayList<>();
        TrafficMonitor traffic = new TrafficMonitor(total -> warnings.add("limit at " + Block3Network.formatBytes(total)));
        traffic.record(Network.WIFI, 12L << 30);
        traffic.record(Network.MOBILE, 3L << 30);
        traffic.record(Network.MOBILE, 2L << 30);
        traffic.record(Network.MOBILE, 1L << 30);
        out.item("P48", "Wi-Fi " + Block3Network.formatBytes(traffic.total(Network.WIFI)) + ", mobile "
                + Block3Network.formatBytes(traffic.total(Network.MOBILE)) + ", warnings " + warnings);
        AudioPlayer player = new AudioPlayer();
        player.moveTo(PlayerState.INITIALIZED);
        player.moveTo(PlayerState.PREPARED);
        player.moveTo(PlayerState.PLAYING);
        player.moveTo(PlayerState.PAUSED);
        player.moveTo(PlayerState.STOPPED);
        String blocked;
        try {
            player.moveTo(PlayerState.PLAYING);
            blocked = "allowed";
        } catch (IllegalStateException e) {
            blocked = "blocked (" + e.getMessage() + ")";
        }
        out.item("P49", player.history() + "; STOPPED -> PLAYING " + blocked);
        String report = formatCrashReport(new IllegalStateException("Adapter position -1 is invalid"),
                new DeviceInfo("15", 35, "TECNO", "BG6", 17_200_000_000L),
                Instant.parse("2026-09-23T12:34:56Z"), "main");
        out.item("P50", "crash report, first lines: " + report.split("\n")[1] + " | " + report.split("\n")[4]
                + " | " + report.split("\n")[5]);
    }
}
