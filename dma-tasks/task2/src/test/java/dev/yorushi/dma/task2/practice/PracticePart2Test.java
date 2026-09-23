package dev.yorushi.dma.task2.practice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.practice.Block3Network.DeepLink;
import dev.yorushi.dma.task2.practice.Block3Network.EtagEndpoint;
import dev.yorushi.dma.task2.practice.Block3Network.OfflineActionQueue;
import dev.yorushi.dma.task2.practice.Block3Network.QuoteFeed;
import dev.yorushi.dma.task2.practice.Block3Network.SyncDecision;
import dev.yorushi.dma.task2.practice.Block3Network.TransferSpeed;
import dev.yorushi.dma.task2.practice.Block3Network.UserAction;
import dev.yorushi.dma.task2.practice.Block4UiState.BackStack;
import dev.yorushi.dma.task2.practice.Block4UiState.ClickDebouncer;
import dev.yorushi.dma.task2.practice.Block4UiState.ColorRole;
import dev.yorushi.dma.task2.practice.Block4UiState.DialogQueue;
import dev.yorushi.dma.task2.practice.Block4UiState.Empty;
import dev.yorushi.dma.task2.practice.Block4UiState.Failed;
import dev.yorushi.dma.task2.practice.Block4UiState.Loaded;
import dev.yorushi.dma.task2.practice.Block4UiState.Loading;
import dev.yorushi.dma.task2.practice.Block4UiState.Profile;
import dev.yorushi.dma.task2.practice.Block4UiState.Started;
import dev.yorushi.dma.task2.practice.Block4UiState.Success;
import dev.yorushi.dma.task2.practice.Block4UiState.TextColor;
import dev.yorushi.dma.task2.practice.Block4UiState.ThemeMode;
import dev.yorushi.dma.task2.practice.Block4UiState.ThemePalette;
import dev.yorushi.dma.task2.practice.Block5Hardware.AudioPlayer;
import dev.yorushi.dma.task2.practice.Block5Hardware.DeviceInfo;
import dev.yorushi.dma.task2.practice.Block5Hardware.MotionState;
import dev.yorushi.dma.task2.practice.Block5Hardware.Network;
import dev.yorushi.dma.task2.practice.Block5Hardware.PlayerState;
import dev.yorushi.dma.task2.practice.Block5Hardware.TrafficMonitor;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Practical items 21–50 of task 2. */
class PracticePart2Test {

    // ---- Block 3 -------------------------------------------------------------

    @Test
    @DisplayName("P21 deep link parsed into routing parameters")
    void p21() {
        DeepLink link = Block3Network.parseDeepLink("app://shop/product?id=452&source=push&q=red%20shoes");
        assertEquals("app", link.scheme());
        assertEquals("shop", link.host());
        assertEquals("/product", link.path());
        assertEquals("452", link.params().get("id"));
        assertEquals("push", link.params().get("source"));
        assertEquals("red shoes", link.params().get("q"));
    }

    @Test
    @DisplayName("P22 retry policy repeats timeouts up to 3 times")
    void p22() throws Exception {
        List<Long> sleeps = new ArrayList<>();
        int[] calls = {0};
        String result = Block3Network.withRetry(() -> {
            if (++calls[0] <= 3) {
                throw new SocketTimeoutException();
            }
            return "ok";
        }, sleeps::add);
        assertEquals("ok", result);
        assertEquals(4, calls[0]);
        assertEquals(Arrays.asList(500L, 1000L, 2000L), sleeps);

        calls[0] = 0;
        assertThrows(SocketTimeoutException.class, () -> Block3Network.withRetry(() -> {
            calls[0]++;
            throw new SocketTimeoutException();
        }, millis -> { }));
        assertEquals(4, calls[0], "one call plus three retries");

        calls[0] = 0;
        assertThrows(IOException.class, () -> Block3Network.withRetry(() -> {
            calls[0]++;
            throw new IOException("not a timeout");
        }, millis -> { }));
        assertEquals(1, calls[0], "non-timeout failures are not retried");
    }

    @Test
    @DisplayName("P23 offline actions flushed in one batch on reconnect")
    void p23() {
        List<List<UserAction>> batches = new ArrayList<>();
        OfflineActionQueue queue = new OfflineActionQueue(batches::add);
        queue.submit(new UserAction("like", "1", ""));
        queue.submit(new UserAction("comment", "1", "hi"));
        assertTrue(batches.isEmpty());
        queue.setOnline(true);
        assertEquals(1, batches.size());
        assertEquals(2, batches.get(0).size());
        queue.submit(new UserAction("like", "2", ""));
        assertEquals(2, batches.size(), "online actions go out immediately");
        assertEquals(0, queue.pendingCount());
    }

    @ParameterizedTest(name = "local {0}, server {1} -> {2}")
    @CsvSource({"1, 2, UPDATE_LOCAL", "3, 2, PUSH_LOCAL", "2, 2, IN_SYNC"})
    @DisplayName("P24 conflict resolver")
    void p24(long local, long server, SyncDecision decision) {
        assertEquals(decision, Block3Network.resolveConflict(local, server));
    }

    @Test
    @DisplayName("P25 download speed in KB/s and Mbit/s")
    void p25() {
        TransferSpeed speed = Block3Network.downloadSpeed(1_000_000, 1000);
        assertEquals(976.5625, speed.kilobytesPerSecond(), 1e-9);
        assertEquals(8.0, speed.megabitsPerSecond(), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> Block3Network.downloadSpeed(1, 0));
    }

    @Test
    @DisplayName("P26 next page from the Link header")
    void p26() {
        assertEquals(OptionalInt.of(3),
                Block3Network.nextPageFromLinkHeader("<https://api.com/items?page=3>; rel=\"next\""));
        assertEquals(OptionalInt.of(8), Block3Network.nextPageFromLinkHeader(
                "<https://api.com/items?page=6>; rel=\"prev\", <https://api.com/items?sort=asc&page=8>; rel=\"next\""));
        assertEquals(OptionalInt.empty(),
                Block3Network.nextPageFromLinkHeader("<https://api.com/items?page=1>; rel=\"prev\""));
        assertEquals(OptionalInt.empty(), Block3Network.nextPageFromLinkHeader(null));
    }

    @Test
    @DisplayName("P27 matching ETag returns 304 without loading the body")
    void p27() {
        int[] loads = {0};
        EtagEndpoint endpoint = new EtagEndpoint("\"abc\"", () -> {
            loads[0]++;
            return "body".getBytes(StandardCharsets.UTF_8);
        });
        assertEquals(304, endpoint.get("\"abc\"").status());
        assertEquals(0, loads[0]);
        assertEquals(200, endpoint.get("\"old\"").status());
        assertEquals(1, loads[0]);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({"0, 0 B", "1023, 1023 B", "1024, 1.0 KB", "1536, 1.5 KB", "1048576, 1.0 MB", "1073741824, 1.0 GB"})
    @DisplayName("P28 human-readable byte counts")
    void p28(long bytes, String expected) {
        assertEquals(expected, Block3Network.formatBytes(bytes));
    }

    @Test
    @DisplayName("P29 quote feed notifies listeners on every tick")
    void p29() {
        QuoteFeed feed = new QuoteFeed("EUR/RUB", 100, 500, 3);
        List<Long> stamps = new ArrayList<>();
        feed.subscribe((pair, price, ts) -> {
            assertEquals("EUR/RUB", pair);
            assertTrue(price > 0);
            stamps.add(ts);
        });
        feed.emit(3);
        assertEquals(Arrays.asList(500L, 1000L, 1500L), stamps);
    }

    @Test
    @DisplayName("P30 required JSON fields checked for null")
    void p30() {
        Map<String, Object> json = new HashMap<>();
        json.put("id", 1);
        json.put("name", null);
        assertEquals(Arrays.asList("name", "email"),
                Block3Network.missingRequiredFields(json, Arrays.asList("id", "name", "email")));
        assertEquals(Collections.singletonList("id"),
                Block3Network.missingRequiredFields(null, Collections.singletonList("id")));
    }

    // ---- Block 4 -------------------------------------------------------------

    @Test
    @DisplayName("P31 UI state reducer covers Loading, Success, Empty, Error")
    void p31() {
        assertInstanceOf(Loading.class, Block4UiState.reduce(new Started<String>()));
        assertInstanceOf(Success.class, Block4UiState.reduce(new Loaded<>(Collections.singletonList("x"))));
        assertInstanceOf(Empty.class, Block4UiState.reduce(new Loaded<String>(Collections.emptyList())));
        assertEquals("error banner: boom", Block4UiState.render(Block4UiState.reduce(new Failed<String>("boom"))));
    }

    @Test
    @DisplayName("P32 click debouncer drops clicks within 500 ms")
    void p32() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        ClickDebouncer debouncer = new ClickDebouncer(clock);
        int[] runs = {0};
        assertTrue(debouncer.onClick(() -> runs[0]++));
        clock.advance(Duration.ofMillis(499));
        assertFalse(debouncer.onClick(() -> runs[0]++));
        clock.advance(Duration.ofMillis(1));
        assertTrue(debouncer.onClick(() -> runs[0]++));
        assertEquals(2, runs[0]);
    }

    @Test
    @DisplayName("P33 back stack push, pop and popToRoot")
    void p33() {
        BackStack<String> stack = new BackStack<>();
        stack.push("Home");
        stack.push("A");
        stack.push("B");
        assertEquals("B", stack.pop());
        assertEquals("A", stack.peek());
        stack.push("C");
        stack.popToRoot();
        assertEquals(1, stack.size());
        assertEquals("Home", stack.peek());
        stack.pop();
        assertNull(stack.peek());
        assertThrows(java.util.NoSuchElementException.class, stack::pop);
    }

    @Test
    @DisplayName("P34 theme palette returns HEX per mode")
    void p34() {
        assertEquals("#FFFFFF", new ThemePalette(ThemeMode.LIGHT).hex(ColorRole.BACKGROUND));
        assertEquals("#121212", new ThemePalette(ThemeMode.DARK).hex(ColorRole.BACKGROUND));
        for (ColorRole role : ColorRole.values()) {
            assertTrue(new ThemePalette(ThemeMode.DARK).hex(role).matches("#[0-9A-F]{6}"));
        }
    }

    @Test
    @DisplayName("P35 profile completeness percentage")
    void p35() {
        assertEquals(0, Block4UiState.profileCompleteness(new Profile(null, "", " ", null, null)));
        assertEquals(40, Block4UiState.profileCompleteness(new Profile("a", null, "p", null, null)));
        assertEquals(100, Block4UiState.profileCompleteness(new Profile("a", "b", "c", "d", "e")));
    }

    @Test
    @DisplayName("P36 YIQ contrast picks black or white text")
    void p36() {
        assertEquals(TextColor.BLACK, Block4UiState.readableTextOn(255, 255, 255));
        assertEquals(TextColor.WHITE, Block4UiState.readableTextOn(0, 0, 0));
        assertEquals(TextColor.BLACK, Block4UiState.readableTextOn(128, 128, 128));
        assertEquals(TextColor.WHITE, Block4UiState.readableTextOn(127, 127, 127));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({"950, 950", "1000, 1K", "1200, 1.2K", "1999, 1.9K", "1500000, 1.5M", "2000000000, 2B", "-1200, -1.2K"})
    @DisplayName("P37 like counter abbreviations")
    void p37(long count, String expected) {
        assertEquals(expected, Block4UiState.formatCount(count));
    }

    @Test
    @DisplayName("P38 dialog queue never overlaps dialogs")
    void p38() {
        DialogQueue queue = new DialogQueue();
        queue.show("A");
        queue.show("B");
        queue.show("C");
        assertEquals("A", queue.visible());
        queue.dismiss();
        assertEquals("B", queue.visible());
        queue.dismiss();
        queue.dismiss();
        assertNull(queue.visible());
        assertEquals(Arrays.asList("A", "B", "C"), queue.shownInOrder());
    }

    @Test
    @DisplayName("P39 pay button needs items, payment method and confirmed address")
    void p39() {
        assertTrue(Block4UiState.isPayButtonEnabled(1, true, true));
        assertFalse(Block4UiState.isPayButtonEnabled(0, true, true));
        assertFalse(Block4UiState.isPayButtonEnabled(1, false, true));
        assertFalse(Block4UiState.isPayButtonEnabled(1, true, false));
    }

    @Test
    @DisplayName("P40 exceptions translated to user hints, Russian when requested")
    void p40() {
        Locale ru = Locale.forLanguageTag("ru");
        String timeoutRu = Block4UiState.translateError(new TimeoutException(), ru);
        String noNetRu = Block4UiState.translateError(new UnknownHostException(), ru);
        assertTrue(timeoutRu.startsWith("Сервер"), timeoutRu);
        assertTrue(noNetRu.startsWith("Нет"), noNetRu);
        assertEquals(timeoutRu, Block4UiState.translateError(new SocketTimeoutException(), ru));
        assertTrue(Block4UiState.translateError(new UnknownHostException(), Locale.ENGLISH)
                .startsWith("No internet connection"));
        assertTrue(Block4UiState.translateError(new IllegalStateException(), Locale.ENGLISH)
                .startsWith("Something went wrong"));
    }

    // ---- Block 5 -------------------------------------------------------------

    @Test
    @DisplayName("P41 haversine distance")
    void p41() {
        assertEquals(0.0, Block5Hardware.haversineMeters(55.75, 37.62, 55.75, 37.62), 1e-6);
        // One degree of latitude is about 111.2 km on the mean-radius sphere.
        assertEquals(111_195, Block5Hardware.haversineMeters(0, 0, 1, 0), 1.0);
        // Moscow to Saint Petersburg, known great-circle distance about 633 km.
        assertEquals(633_000, Block5Hardware.haversineMeters(55.7558, 37.6173, 59.9343, 30.3351), 3_000);
    }

    @Test
    @DisplayName("P42 geofence membership")
    void p42() {
        assertTrue(Block5Hardware.isInsideGeofence(55.7558, 37.6173, 55.7558, 37.6173, 1));
        assertTrue(Block5Hardware.isInsideGeofence(0, 0.0089, 0, 0, 1000));
        assertFalse(Block5Hardware.isInsideGeofence(0, 0.0091, 0, 0, 1000));
    }

    @ParameterizedTest(name = "{0}% -> {1} s")
    @CsvSource({"100, 5", "51, 5", "50, 30", "15, 30", "14, 300", "0, 300"})
    @DisplayName("P43 battery-aware GPS interval")
    void p43(int battery, int seconds) {
        assertEquals(seconds, Block5Hardware.gpsIntervalSeconds(battery));
    }

    @Test
    @DisplayName("P44 free fall and impact detection")
    void p44() {
        assertEquals(MotionState.NORMAL, Block5Hardware.detectFall(0, 0, 9.81));
        assertEquals(MotionState.FREE_FALL, Block5Hardware.detectFall(0.3, 0.3, 0.3));
        assertEquals(MotionState.IMPACT, Block5Hardware.detectFall(20, 20, 0));
    }

    @Test
    @DisplayName("P45 step counting by local maxima above the barrier")
    void p45() {
        assertEquals(2, Block5Hardware.countSteps(new double[] {9, 12, 9, 10, 13, 9}, 11));
        assertEquals(0, Block5Hardware.countSteps(new double[] {9, 10, 9}, 11));
        assertEquals(1, Block5Hardware.countSteps(new double[] {9, 12, 12, 9}, 11), "plateau counts once");
    }

    @Test
    @DisplayName("P46 logarithmic brightness mapping")
    void p46() {
        assertEquals(0, Block5Hardware.brightnessPercent(0));
        assertEquals(100, Block5Hardware.brightnessPercent(10_000));
        assertEquals(100, Block5Hardware.brightnessPercent(120_000));
        assertTrue(Block5Hardware.brightnessPercent(100) > Block5Hardware.brightnessPercent(10));
    }

    @Test
    @DisplayName("P47 heavy sync only on Wi-Fi while charging")
    void p47() {
        assertTrue(Block5Hardware.canStartHeavySync(true, true));
        assertFalse(Block5Hardware.canStartHeavySync(true, false));
        assertFalse(Block5Hardware.canStartHeavySync(false, true));
    }

    @Test
    @DisplayName("P48 traffic monitor warns once at 5 GB of mobile data")
    void p48() {
        List<Long> warnings = new ArrayList<>();
        TrafficMonitor monitor = new TrafficMonitor(warnings::add);
        monitor.record(Network.WIFI, 10L << 30);
        assertTrue(warnings.isEmpty(), "Wi-Fi does not count towards the mobile limit");
        monitor.record(Network.MOBILE, 4L << 30);
        monitor.record(Network.MOBILE, 1L << 30);
        monitor.record(Network.MOBILE, 1L << 30);
        assertEquals(1, warnings.size());
        assertEquals(6L << 30, monitor.total(Network.MOBILE));
    }

    @Test
    @DisplayName("P49 audio player blocks illegal transitions")
    void p49() {
        AudioPlayer player = new AudioPlayer();
        assertThrows(IllegalStateException.class, () -> player.moveTo(PlayerState.PLAYING));
        player.moveTo(PlayerState.INITIALIZED);
        player.moveTo(PlayerState.PREPARED);
        player.moveTo(PlayerState.PLAYING);
        player.moveTo(PlayerState.PAUSED);
        player.moveTo(PlayerState.PLAYING);
        player.moveTo(PlayerState.STOPPED);
        assertFalse(player.canMoveTo(PlayerState.PLAYING));
        player.moveTo(PlayerState.PREPARED);
        assertEquals(PlayerState.PREPARED, player.state());
    }

    @Test
    @DisplayName("P50 crash report carries device context and stack trace")
    void p50() {
        String report = Block5Hardware.formatCrashReport(new IllegalStateException("boom"),
                new DeviceInfo("15", 35, "Google", "Pixel 7", 5L << 30),
                Instant.parse("2026-01-01T00:00:00Z"), "main");
        assertTrue(report.contains("Android:  15 (API 35)"));
        assertTrue(report.contains("Device:   Google Pixel 7"));
        assertTrue(report.contains("Storage:  5.0 GB free"));
        assertTrue(report.contains("java.lang.IllegalStateException: boom"));
        assertTrue(report.contains("at dev.yorushi.dma.task2.practice.PracticePart2Test.p50"));
    }
}
