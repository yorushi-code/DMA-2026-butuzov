package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

/** Theme 8 — collections and generics. */
public final class Theme8Collections {

    private Theme8Collections() {
    }

    // ---- T8.1 --------------------------------------------------------------

    /** T8.1: removes duplicate phone numbers while keeping the first-seen order. */
    public static List<String> dedupeContacts(List<String> phones) {
        return new ArrayList<>(new LinkedHashSet<>(phones));
    }

    // ---- T8.2 --------------------------------------------------------------

    /** T8.2: FIFO queue of pending sync requests. */
    public static final class SyncQueue {
        private final Queue<String> pending = new ArrayDeque<>();

        public void enqueue(String request) {
            pending.offer(request);
        }

        /** Hands requests to {@code processor} in arrival order until the queue is empty. */
        public int drain(Consumer<String> processor) {
            int processed = 0;
            String request;
            while ((request = pending.poll()) != null) {
                processor.accept(request);
                processed++;
            }
            return processed;
        }

        public int size() {
            return pending.size();
        }
    }

    // ---- T8.3 --------------------------------------------------------------

    /** T8.3: generic API envelope. */
    public static final class ApiResponse<T> {
        private final int statusCode;
        private final T data;
        private final String errorMessage;

        private ApiResponse(int statusCode, T data, String errorMessage) {
            this.statusCode = statusCode;
            this.data = data;
            this.errorMessage = errorMessage;
        }

        public static <T> ApiResponse<T> success(int statusCode, T data) {
            return new ApiResponse<>(statusCode, data, null);
        }

        public static <T> ApiResponse<T> error(int statusCode, String errorMessage) {
            return new ApiResponse<>(statusCode, null, errorMessage);
        }

        public boolean isSuccessful() {
            return statusCode >= 200 && statusCode < 300 && errorMessage == null;
        }

        public int statusCode() {
            return statusCode;
        }

        public T data() {
            return data;
        }

        public String errorMessage() {
            return errorMessage;
        }
    }

    // ---- T8.4 --------------------------------------------------------------

    public record Product(String name, double price, double rating) {
    }

    /** T8.4: cheapest first; among equal prices the best rated comes first. */
    public static final Comparator<Product> BY_PRICE_THEN_RATING =
            Comparator.comparingDouble(Product::price)
                    .thenComparing(Comparator.comparingDouble(Product::rating).reversed());

    public static List<Product> sortCatalog(List<Product> products) {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(BY_PRICE_THEN_RATING);
        return sorted;
    }

    // ---- T8.5 --------------------------------------------------------------

    /**
     * T8.5: least-recently-used cache of open screens. An access-ordered
     * {@link LinkedHashMap} moves every touched entry to the tail, so the head is
     * always the eviction candidate.
     */
    public static final class ScreenCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public ScreenCache(int capacity) {
            super(16, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    public static void demo(Report out) {
        out.section("Theme 8. Collections and generics");
        out.item("T8.1", dedupeContacts(Arrays.asList("+79990001122", "+79161234567", "+79990001122",
                "+79035556677", "+79161234567")).toString());
        SyncQueue queue = new SyncQueue();
        queue.enqueue("POST /likes/41");
        queue.enqueue("PUT /profile");
        queue.enqueue("DELETE /draft/7");
        List<String> order = new ArrayList<>();
        queue.drain(order::add);
        out.item("T8.2", "processed in FIFO order " + order);
        ApiResponse<String> ok = ApiResponse.success(200, "{\"id\":7}");
        ApiResponse<String> fail = ApiResponse.error(503, "Service unavailable");
        out.item("T8.3", "200 successful=" + ok.isSuccessful() + ", 503 successful=" + fail.isSuccessful()
                + " (" + fail.errorMessage() + ")");
        List<Product> sorted = sortCatalog(Arrays.asList(new Product("Case", 990, 4.1),
                new Product("Charger", 1490, 4.8), new Product("Cable", 990, 4.7), new Product("Stand", 490, 3.9)));
        StringBuilder names = new StringBuilder();
        for (Product p : sorted) {
            names.append(p.name()).append('(').append((int) p.price()).append(',').append(p.rating()).append(") ");
        }
        out.item("T8.4", names.toString().trim());
        ScreenCache<String, String> cache = new ScreenCache<>(5);
        for (String screen : Arrays.asList("feed", "search", "cart", "profile", "orders")) {
            cache.put(screen, screen + "Fragment");
        }
        cache.get("feed");
        cache.put("settings", "SettingsFragment");
        out.item("T8.5", "capacity 5, touched feed, opened settings -> " + cache.keySet() + " (search evicted)");
    }
}
