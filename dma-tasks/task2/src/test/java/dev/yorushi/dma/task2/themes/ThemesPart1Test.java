package dev.yorushi.dma.task2.themes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.themes.Theme1Primitives.PermissionFlags;
import dev.yorushi.dma.task2.themes.Theme1Primitives.TimeBreakdown;
import dev.yorushi.dma.task2.themes.Theme2ControlFlow.PinResult;
import dev.yorushi.dma.task2.themes.Theme4Methods.Node;
import dev.yorushi.dma.task2.themes.Theme5Encapsulation.BadgeCounter;
import dev.yorushi.dma.task2.themes.Theme5Encapsulation.CartItem;
import dev.yorushi.dma.task2.themes.Theme5Encapsulation.GeoPoint;
import dev.yorushi.dma.task2.themes.Theme5Encapsulation.SessionTracker;
import dev.yorushi.dma.task2.themes.Theme5Encapsulation.SettingsModel;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Themes 1–5 of task 2. */
class ThemesPart1Test {

    // ---- Theme 1 -------------------------------------------------------------

    @ParameterizedTest(name = "{0} dp x {1} = {2} px")
    @CsvSource({"48, 1.5, 72", "48, 2.0, 96", "48, 3.0, 144", "1, 2.75, 3"})
    @DisplayName("T1.1 dp -> px conversion")
    void t1_1(float dp, float density, int px) {
        assertEquals(px, Theme1Primitives.dpToPx(dp, density));
        assertThrows(IllegalArgumentException.class, () -> Theme1Primitives.dpToPx(10, 0));
    }

    @Test
    @DisplayName("T1.2 millisecond timestamp split into hours, minutes and seconds")
    void t1_2() {
        TimeBreakdown t = Theme1Primitives.breakdown(7_509_000L);
        assertEquals(2, t.totalHours());
        assertEquals(125, t.totalMinutes());
        assertEquals(7509, t.totalSeconds());
        assertEquals("02:05:09", t.hms());
    }

    @Test
    @DisplayName("T1.3 battery runtime estimate")
    void t1_3() {
        assertEquals(12.5, Theme1Primitives.batteryLifeHours(5000, 120, 280), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> Theme1Primitives.batteryLifeHours(5000, 0, 0));
    }

    @Test
    @DisplayName("T1.4 coordinate range validation with && and ||")
    void t1_4() {
        assertTrue(Theme1Primitives.isValidCoordinate(90, -180));
        assertTrue(Theme1Primitives.isValidCoordinate(-90, 180));
        assertFalse(Theme1Primitives.isValidCoordinate(90.0001, 0));
        assertFalse(Theme1Primitives.isValidCoordinate(0, -180.5));
        assertFalse(Theme1Primitives.isValidCoordinate(Double.NaN, 0));
    }

    @Test
    @DisplayName("T1.5 bitwise permission flags")
    void t1_5() {
        PermissionFlags flags = new PermissionFlags().grant(PermissionFlags.CAMERA).grant(PermissionFlags.LOCATION);
        assertEquals(3, flags.mask());
        flags.revoke(PermissionFlags.CAMERA);
        assertFalse(flags.has(PermissionFlags.CAMERA));
        assertTrue(flags.has(PermissionFlags.LOCATION));
        assertFalse(flags.has(PermissionFlags.LOCATION | PermissionFlags.STORAGE));
    }

    // ---- Theme 2 -------------------------------------------------------------

    @Test
    @DisplayName("T2.1 screen orientation")
    void t2_1() {
        assertEquals("PORTRAIT", Theme2ControlFlow.orientation(1080, 2400));
        assertEquals("LANDSCAPE", Theme2ControlFlow.orientation(2400, 1080));
        assertEquals("SQUARE", Theme2ControlFlow.orientation(500, 500));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({"100, Informational", "204, Success", "301, Redirection", "404, Client error", "500, Server error"})
    @DisplayName("T2.2 HTTP status category via switch")
    void t2_2(int code, String category) {
        assertEquals(category, Theme2ControlFlow.httpCategory(code));
    }

    @Test
    @DisplayName("T2.3 exponential backoff doubles from 1 s to 16 s")
    void t2_3() {
        assertEquals(Arrays.asList(1000L, 2000L, 4000L, 8000L, 16000L), Theme2ControlFlow.backoffDelays(a -> false));
        assertEquals(Collections.emptyList(), Theme2ControlFlow.backoffDelays(a -> true));
    }

    @Test
    @DisplayName("T2.4 continue on -1, break on 0")
    void t2_4() {
        assertEquals(Arrays.asList(5, 7, 9), Theme2ControlFlow.readMessageIds(new int[] {5, -1, 7, 9, 0, 11}));
    }

    @Test
    @DisplayName("T2.5 PIN entry limited to three attempts")
    void t2_5() {
        PinResult first = Theme2ControlFlow.enterPin("1234", Arrays.asList("1234").iterator());
        assertTrue(first.unlocked());
        assertEquals(1, first.attemptsUsed());
        PinResult locked = Theme2ControlFlow.enterPin("1234", Arrays.asList("1", "2", "3", "1234").iterator());
        assertFalse(locked.unlocked());
        assertEquals(3, locked.attemptsUsed());
    }

    // ---- Theme 3 -------------------------------------------------------------

    @Test
    @DisplayName("T3.1 search query normalisation")
    void t3_1() {
        assertEquals("wireless headphones pro", Theme3ArraysStrings.normalizeQuery("  Wireless \t HEADPHONES   pro "));
        assertEquals("", Theme3ArraysStrings.normalizeQuery(null));
    }

    @Test
    @DisplayName("T3.2 in-place reversal of animation frames")
    void t3_2() {
        String[] odd = {"a", "b", "c"};
        Theme3ArraysStrings.reverseInPlace(odd);
        assertArrayEquals(new String[] {"c", "b", "a"}, odd);
        String[] even = {"1", "2", "3", "4"};
        Theme3ArraysStrings.reverseInPlace(even);
        assertArrayEquals(new String[] {"4", "3", "2", "1"}, even);
    }

    @Test
    @DisplayName("T3.3 card number masking")
    void t3_3() {
        assertEquals("**** **** **** 1234", Theme3ArraysStrings.maskCardNumber("5536913712341234"));
        assertThrows(IllegalArgumentException.class, () -> Theme3ArraysStrings.maskCardNumber("1234"));
    }

    @Test
    @DisplayName("T3.4 largest jump between neighbouring samples")
    void t3_4() {
        assertEquals(12.0, Theme3ArraysStrings.maxSpike(new double[] {0, 1, -11, -10, -9}), 1e-9);
    }

    @Test
    @DisplayName("T3.5 GET parameters built with StringBuilder")
    void t3_5() {
        assertEquals("?key1=val1&key2=val2",
                Theme3ArraysStrings.buildQuery(new String[] {"key1", "key2"}, new String[] {"val1", "val2"}));
        assertEquals("?q=a+b%26c", Theme3ArraysStrings.buildQuery(new String[] {"q"}, new String[] {"a b&c"}));
    }

    // ---- Theme 4 -------------------------------------------------------------

    @Test
    @DisplayName("T4.1 overloaded e-mail validator")
    void t4_1() {
        assertTrue(Theme4Methods.isValid("user@corp.io"));
        assertFalse(Theme4Methods.isValid("user@corp.io", true));
        assertTrue(Theme4Methods.isValid("user@gmail.com", true));
        assertFalse(Theme4Methods.isValid("not-an-email"));
    }

    @Test
    @DisplayName("T4.2 currency formatting")
    void t4_2() {
        assertEquals("12 499.90 ₽", Theme4Methods.formatPrice(12499.9, "₽"));
        assertEquals("0.00 $", Theme4Methods.formatPrice(0, "$"));
    }

    @Test
    @DisplayName("T4.3 varargs cache size in megabytes")
    void t4_3() {
        assertEquals(4.0, Theme4Methods.calculateCache(1_048_576L, 2_621_440L, 524_288L), 1e-9);
        assertEquals(0.0, Theme4Methods.calculateCache(), 1e-9);
    }

    @Test
    @DisplayName("T4.4 recursive count of nested elements")
    void t4_4() {
        Node tree = Node.folder("root",
                Node.folder("a", Node.file("1"), Node.folder("b", Node.file("2"))),
                Node.file("3"));
        assertEquals(5, Theme4Methods.countNested(tree));
        assertEquals(0, Theme4Methods.countNested(Node.folder("empty")));
    }

    @ParameterizedTest(name = "{0} vs {1} = {2}")
    @CsvSource({"1.12.0, 1.9.4, 1", "1.9.4, 1.12.0, -1", "1.2, 1.2.0, 0", "2.0.1, 2.0, 1"})
    @DisplayName("T4.5 numeric version comparison")
    void t4_5(String v1, String v2, int expected) {
        assertEquals(expected, Theme4Methods.compareVersions(v1, v2));
    }

    // ---- Theme 5 -------------------------------------------------------------

    @Test
    @DisplayName("T5.1 settings model validates volume in the setter")
    void t5_1() {
        SettingsModel settings = new SettingsModel();
        settings.setVolumeLevel(0);
        settings.setVolumeLevel(100);
        assertThrows(IllegalArgumentException.class, () -> settings.setVolumeLevel(101));
        assertThrows(IllegalArgumentException.class, () -> settings.setAppLanguage("russian"));
        assertEquals(100, settings.getVolumeLevel());
    }

    @Test
    @DisplayName("T5.2 CartItem record computes its total")
    void t5_2() {
        assertEquals(1497.0, new CartItem("1", "Cable", 499.0, 3).totalPrice(), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> new CartItem("1", "Cable", 499.0, 0));
    }

    @Test
    @DisplayName("T5.3 badge counter never goes negative")
    void t5_3() {
        BadgeCounter badge = new BadgeCounter();
        badge.decrement();
        assertEquals(0, badge.value());
        badge.increment();
        badge.increment();
        assertEquals(2, badge.value());
        assertThrows(IllegalArgumentException.class, () -> badge.set(-1));
    }

    @Test
    @DisplayName("T5.4 session expires after 15 idle minutes")
    void t5_4() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        SessionTracker session = new SessionTracker(clock);
        clock.advance(Duration.ofMinutes(10));
        session.recordActivity();
        clock.advance(Duration.ofMinutes(15));
        assertFalse(session.isExpired());
        clock.advance(Duration.ofSeconds(1));
        assertTrue(session.isExpired());
    }

    @Test
    @DisplayName("T5.5 GeoPoint rejects invalid coordinates")
    void t5_5() {
        GeoPoint point = new GeoPoint(54.0105, 38.2917);
        assertEquals(54.0105, point.latitude(), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(0, 200));
    }
}
