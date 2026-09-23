package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.Report;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

/** Theme 5 — classes, objects and encapsulation. */
public final class Theme5Encapsulation {

    private Theme5Encapsulation() {
    }

    // ---- T5.1 --------------------------------------------------------------

    /** T5.1: settings screen model; volume is validated in its setter. */
    public static final class SettingsModel {
        private boolean isDarkMode;
        private int volumeLevel = 50;
        private String appLanguage = "en";

        public boolean isDarkMode() {
            return isDarkMode;
        }

        public void setDarkMode(boolean darkMode) {
            isDarkMode = darkMode;
        }

        public int getVolumeLevel() {
            return volumeLevel;
        }

        public void setVolumeLevel(int volumeLevel) {
            if (volumeLevel < 0 || volumeLevel > 100) {
                throw new IllegalArgumentException("volume must be within 0..100: " + volumeLevel);
            }
            this.volumeLevel = volumeLevel;
        }

        public String getAppLanguage() {
            return appLanguage;
        }

        public void setAppLanguage(String appLanguage) {
            if (appLanguage == null || !appLanguage.matches("[a-z]{2}")) {
                throw new IllegalArgumentException("language must be an ISO 639-1 code: " + appLanguage);
            }
            this.appLanguage = appLanguage;
        }
    }

    // ---- T5.2 --------------------------------------------------------------

    /** T5.2: immutable cart line with its own total. */
    public record CartItem(String id, String title, double price, int count) {
        public CartItem {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(title, "title");
            if (price < 0) {
                throw new IllegalArgumentException("price cannot be negative");
            }
            if (count < 1) {
                throw new IllegalArgumentException("count must be at least 1");
            }
        }

        public double totalPrice() {
            return price * count;
        }
    }

    // ---- T5.3 --------------------------------------------------------------

    /** T5.3: unread push counter that can never go below zero. */
    public static final class BadgeCounter {
        private int value;

        public int value() {
            return value;
        }

        public void increment() {
            value++;
        }

        public void decrement() {
            if (value > 0) {
                value--;
            }
        }

        public void set(int newValue) {
            if (newValue < 0) {
                throw new IllegalArgumentException("badge counter cannot be negative: " + newValue);
            }
            value = newValue;
        }
    }

    // ---- T5.4 --------------------------------------------------------------

    /** T5.4: session that expires after 15 minutes without user activity. */
    public static final class SessionTracker {
        public static final Duration TIMEOUT = Duration.ofMinutes(15);

        private final Clock clock;
        private final Instant loginTime;
        private Instant lastActivity;

        public SessionTracker(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
            this.loginTime = clock.instant();
            this.lastActivity = loginTime;
        }

        public void recordActivity() {
            lastActivity = clock.instant();
        }

        public boolean isExpired() {
            return Duration.between(lastActivity, clock.instant()).compareTo(TIMEOUT) > 0;
        }

        public Instant loginTime() {
            return loginTime;
        }
    }

    // ---- T5.5 --------------------------------------------------------------

    /** T5.5: immutable geographic point validated at construction. */
    public static final class GeoPoint {
        private final double latitude;
        private final double longitude;

        public GeoPoint(double latitude, double longitude) {
            if (!Theme1Primitives.isValidCoordinate(latitude, longitude)) {
                throw new IllegalArgumentException(String.format(Locale.ROOT,
                        "invalid coordinate (%.4f, %.4f)", latitude, longitude));
            }
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double latitude() {
            return latitude;
        }

        public double longitude() {
            return longitude;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "GeoPoint(%.4f, %.4f)", latitude, longitude);
        }
    }

    public static void demo(Report out) {
        out.section("Theme 5. Encapsulation");
        SettingsModel settings = new SettingsModel();
        settings.setVolumeLevel(80);
        String rejected;
        try {
            settings.setVolumeLevel(140);
            rejected = "accepted";
        } catch (IllegalArgumentException e) {
            rejected = "rejected";
        }
        out.item("T5.1", "volume 80 stored, volume 140 " + rejected + "; current=" + settings.getVolumeLevel());
        CartItem item = new CartItem("sku-17", "USB-C cable", 499.0, 3);
        out.item("T5.2", item.title() + " x" + item.count() + " = " + item.totalPrice());
        BadgeCounter badge = new BadgeCounter();
        badge.increment();
        badge.decrement();
        badge.decrement();
        out.item("T5.3", "+1, -1, -1 -> badge=" + badge.value() + " (never negative)");
        MutableClock clock = new MutableClock(Instant.parse("2026-09-23T12:00:00Z"));
        SessionTracker session = new SessionTracker(clock);
        clock.advance(Duration.ofMinutes(14));
        boolean after14 = session.isExpired();
        clock.advance(Duration.ofMinutes(2));
        out.item("T5.4", "expired after 14 min=" + after14 + ", after 16 min=" + session.isExpired());
        String invalid;
        try {
            new GeoPoint(120.0, 0.0);
            invalid = "accepted";
        } catch (IllegalArgumentException e) {
            invalid = "rejected";
        }
        out.item("T5.5", new GeoPoint(54.0105, 38.2917) + " created, latitude 120 " + invalid);
    }
}
