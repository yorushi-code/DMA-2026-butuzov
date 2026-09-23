package dev.yorushi.dma.task2.themes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.yorushi.dma.task2.themes.Theme6Inheritance.AnalyticsService;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.AnnualDiscountSubscription;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.BaseScreen;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.ButtonComponent;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.ClickEvent;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.DeviceSensor;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.FamilySubscription;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.GyroscopeSensor;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.HomeScreen;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.ImageComponent;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.LightSensor;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.LoginScreen;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.MonthlySubscription;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.PurchaseEvent;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.ScreenViewEvent;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.SettingsScreen;
import dev.yorushi.dma.task2.themes.Theme6Inheritance.Subscription;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.BackgroundTaskListener;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.ImageLoadCallback;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.KeyValueStorage;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.MediaFile;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.MemoryStorage;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.Playable;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.PredicateValidator;
import dev.yorushi.dma.task2.themes.Theme7Interfaces.Shareable;
import dev.yorushi.dma.task2.themes.Theme8Collections.ApiResponse;
import dev.yorushi.dma.task2.themes.Theme8Collections.Product;
import dev.yorushi.dma.task2.themes.Theme8Collections.ScreenCache;
import dev.yorushi.dma.task2.themes.Theme8Collections.SyncQueue;
import dev.yorushi.dma.task2.themes.Theme9Exceptions.InvalidUserDataException;
import dev.yorushi.dma.task2.themes.Theme9Exceptions.NoInternetException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Themes 6–9 of task 2. */
class ThemesPart2Test {

    // ---- Theme 6 -------------------------------------------------------------

    @Test
    @DisplayName("T6.1 screen hierarchy shares and overrides lifecycle hooks")
    void t6_1() {
        List<BaseScreen> screens = Arrays.asList(new LoginScreen(), new HomeScreen(), new SettingsScreen());
        for (BaseScreen screen : screens) {
            assertTrue(screen.onOpen().startsWith(screen.title() + " opened"));
            assertTrue(screen.isOpen());
            assertTrue(screen.onClose().endsWith(screen.title() + " closed"));
            assertFalse(screen.isOpen());
        }
        assertTrue(new SettingsScreen().onClose().startsWith("preferences saved"));
    }

    @Test
    @DisplayName("T6.2 analytics service dispatches on the event type")
    void t6_2() {
        AnalyticsService service = new AnalyticsService();
        service.track(new ClickEvent("btn"));
        service.track(new PurchaseEvent("sku", 10));
        service.track(new ScreenViewEvent("Cart", 1500));
        assertEquals(Arrays.asList("click element=btn", "purchase sku=sku amount=10.00 revenue=true",
                "screen_view name=Cart time=1500ms"), service.sent());
    }

    @Test
    @DisplayName("T6.3 sensors implement readData polymorphically")
    void t6_3() {
        DeviceSensor gyro = new GyroscopeSensor(0.1, 0.2, 0.3);
        DeviceSensor light = new LightSensor(250f);
        assertEquals("gyro x=0.10 y=0.20 z=0.30 rad/s", gyro.readData());
        assertEquals("light 250 lx", light.readData());
    }

    @Test
    @DisplayName("T6.4 drawScreen renders any UiComponent")
    void t6_4() {
        assertEquals(Arrays.asList("Button[1] 'Pay'", "Image[2] https://x/y.png"),
                Theme6Inheritance.drawScreen(Arrays.asList(new ButtonComponent(1, "Pay"),
                        new ImageComponent(2, "https://x/y.png"))));
    }

    @Test
    @DisplayName("T6.5 subscription plans price differently")
    void t6_5() {
        Subscription monthly = new MonthlySubscription();
        assertEquals(299.0, monthly.monthlyCost(), 1e-9);
        assertEquals(299.0 * 12, monthly.costFor(12), 1e-9);
        assertEquals(299.0, new FamilySubscription(1).monthlyCost(), 1e-9);
        assertEquals(657.8, new FamilySubscription(4).monthlyCost(), 1e-9);
        assertEquals(224.25, new AnnualDiscountSubscription().monthlyCost(), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> new FamilySubscription(7));
    }

    // ---- Theme 7 -------------------------------------------------------------

    @Test
    @DisplayName("T7.1 KeyValueStorage contract via MemoryStorage")
    void t7_1() {
        KeyValueStorage storage = new MemoryStorage();
        storage.save("k", "v");
        assertEquals("v", storage.get("k"));
        storage.clear();
        assertNull(storage.get("k"));
    }

    @Test
    @DisplayName("T7.2 image load callback reports success and error")
    void t7_2() {
        List<String> events = new ArrayList<>();
        ImageLoadCallback callback = new ImageLoadCallback() {
            @Override
            public void onSuccess(String bitmapRef) {
                events.add("ok");
            }

            @Override
            public void onError(Throwable error) {
                events.add("error");
            }
        };
        Theme7Interfaces.loadImage("https://a/b.png", callback);
        Theme7Interfaces.loadImage("ftp://a/b.png", callback);
        assertEquals(Arrays.asList("ok", "error"), events);
    }

    @Test
    @DisplayName("T7.3 default onProgress can be left unimplemented")
    void t7_3() {
        boolean[] completed = {false};
        BackgroundTaskListener onlyCompletion = () -> completed[0] = true;
        Theme7Interfaces.runTask(3, onlyCompletion);
        assertTrue(completed[0]);
    }

    @Test
    @DisplayName("T7.4 MediaFile implements Playable and Shareable")
    void t7_4() {
        MediaFile file = new MediaFile("song.mp3");
        Playable playable = file;
        Shareable shareable = file;
        playable.play();
        assertTrue(file.isPlaying());
        assertTrue(shareable.shareViaBluetooth().contains("song.mp3"));
        playable.stop();
        assertFalse(file.isPlaying());
    }

    @Test
    @DisplayName("T7.5 functional interface tested through lambdas")
    void t7_5() {
        PredicateValidator<Integer> positive = n -> n > 0;
        PredicateValidator<Integer> even = n -> n % 2 == 0;
        assertTrue(positive.and(even).validate(4));
        assertFalse(positive.and(even).validate(3));
        assertFalse(positive.and(even).validate(-2));
    }

    // ---- Theme 8 -------------------------------------------------------------

    @Test
    @DisplayName("T8.1 duplicates removed, first-seen order kept")
    void t8_1() {
        assertEquals(Arrays.asList("c", "a", "b"),
                Theme8Collections.dedupeContacts(Arrays.asList("c", "a", "c", "b", "a")));
    }

    @Test
    @DisplayName("T8.2 sync queue drains in FIFO order")
    void t8_2() {
        SyncQueue queue = new SyncQueue();
        queue.enqueue("1");
        queue.enqueue("2");
        queue.enqueue("3");
        List<String> order = new ArrayList<>();
        assertEquals(3, queue.drain(order::add));
        assertEquals(Arrays.asList("1", "2", "3"), order);
        assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("T8.3 generic ApiResponse")
    void t8_3() {
        ApiResponse<List<Integer>> ok = ApiResponse.success(200, Arrays.asList(1, 2));
        assertTrue(ok.isSuccessful());
        assertEquals(2, ok.data().size());
        ApiResponse<String> notFound = ApiResponse.error(404, "Not found");
        assertFalse(notFound.isSuccessful());
        assertNull(notFound.data());
    }

    @Test
    @DisplayName("T8.4 products sorted by price, then by rating")
    void t8_4() {
        List<Product> sorted = Theme8Collections.sortCatalog(Arrays.asList(new Product("B", 10, 4.0),
                new Product("A", 5, 3.0), new Product("C", 10, 4.9)));
        assertEquals(Arrays.asList("A", "C", "B"), Arrays.asList(sorted.get(0).name(), sorted.get(1).name(),
                sorted.get(2).name()));
    }

    @Test
    @DisplayName("T8.5 screen cache evicts the least recently used of 5")
    void t8_5() {
        ScreenCache<Integer, String> cache = new ScreenCache<>(5);
        for (int i = 1; i <= 5; i++) {
            cache.put(i, "screen" + i);
        }
        cache.get(1);
        cache.put(6, "screen6");
        assertEquals(5, cache.size());
        assertFalse(cache.containsKey(2));
        assertTrue(cache.containsKey(1));
    }

    // ---- Theme 9 -------------------------------------------------------------

    @Test
    @DisplayName("T9.1 checked NoInternetException when offline")
    void t9_1() throws NoInternetException {
        assertThrows(NoInternetException.class, () -> Theme9Exceptions.fetchProfile(false));
        assertTrue(Theme9Exceptions.fetchProfile(true).contains("yorushi"));
    }

    @Test
    @DisplayName("T9.2 config file read with try-with-resources")
    void t9_2(@TempDir File dir) throws IOException {
        File config = new File(dir, "app.conf");
        try (PrintWriter writer = new PrintWriter(config, "UTF-8")) {
            writer.println("# comment");
            writer.println("host = example.com");
            writer.println();
            writer.println("port=8080");
        }
        Map<String, String> values = Theme9Exceptions.readConfig(config);
        assertEquals("example.com", values.get("host"));
        assertEquals("8080", values.get("port"));
        assertEquals(2, values.size());
        assertThrows(IOException.class, () -> Theme9Exceptions.readConfig(new File(dir, "missing.conf")));
    }

    @Test
    @DisplayName("T9.3 unchecked InvalidUserDataException for impossible ages")
    void t9_3() {
        assertEquals(130, Theme9Exceptions.parseAge("130"));
        assertThrows(InvalidUserDataException.class, () -> Theme9Exceptions.parseAge("-1"));
        assertThrows(InvalidUserDataException.class, () -> Theme9Exceptions.parseAge("131"));
        assertThrows(InvalidUserDataException.class, () -> Theme9Exceptions.parseAge("abc"));
    }

    @Test
    @DisplayName("T9.4 separate catch blocks for NPE, IOOBE and Exception")
    void t9_4() {
        String nothing = null;
        List<Integer> empty = Collections.emptyList();
        assertEquals("NullPointerException block", Theme9Exceptions.classifyFailure(() -> nothing.trim()));
        assertEquals("IndexOutOfBoundsException block", Theme9Exceptions.classifyFailure(() -> empty.get(0)));
        assertTrue(Theme9Exceptions.classifyFailure(() -> {
            throw new IllegalStateException();
        }).startsWith("generic Exception block"));
        assertEquals("no error", Theme9Exceptions.classifyFailure(() -> { }));
    }

    @Test
    @DisplayName("T9.5 safe Bundle read falls back to the default")
    void t9_5() {
        Map<String, Object> bundle = new HashMap<>();
        bundle.put("name", "Ann");
        bundle.put("age", 30);
        assertEquals("Ann", Theme9Exceptions.getStringSafe(bundle, "name", "Guest"));
        assertEquals("Guest", Theme9Exceptions.getStringSafe(bundle, "missing", "Guest"));
        assertEquals("Guest", Theme9Exceptions.getStringSafe(bundle, "age", "Guest"));
        assertEquals("Guest", Theme9Exceptions.getStringSafe(null, "name", "Guest"));
    }
}
