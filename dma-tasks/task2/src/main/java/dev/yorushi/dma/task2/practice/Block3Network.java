package dev.yorushi.dma.task2.practice;

import dev.yorushi.dma.task2.Report;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Practical block 3 — networking, parsing and offline sync (items 21–30). */
public final class Block3Network {

    private Block3Network() {
    }

    // ---- P21 ---------------------------------------------------------------

    public record DeepLink(String scheme, String host, String path, Map<String, String> params) {
    }

    /** P21: splits {@code app://shop/product?id=452&source=push} into routing parts. */
    public static DeepLink parseDeepLink(String url) {
        URI uri = URI.create(url);
        Map<String, String> params = new LinkedHashMap<>();
        String query = uri.getRawQuery();
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                String key = eq < 0 ? pair : pair.substring(0, eq);
                String value = eq < 0 ? "" : pair.substring(eq + 1);
                params.put(decode(key), decode(value));
            }
        }
        return new DeepLink(uri.getScheme(), uri.getHost(), uri.getPath(), Collections.unmodifiableMap(params));
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException impossible) {
            throw new AssertionError("UTF-8 is always supported", impossible);
        }
    }

    // ---- P22 ---------------------------------------------------------------

    /** Pause between attempts; injected so tests do not actually sleep. */
    @FunctionalInterface
    public interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }

    /**
     * P22: retries a call up to three times, but only for timeouts. Other failures
     * are not transient, so repeating them would only delay the error.
     */
    public static <T> T withRetry(Callable<T> call, Sleeper sleeper) throws Exception {
        final int maxRetries = 3;
        long backoffMs = 500;
        for (int retry = 0; ; retry++) {
            try {
                return call.call();
            } catch (SocketTimeoutException timeout) {
                if (retry == maxRetries) {
                    throw timeout;
                }
                sleeper.sleep(backoffMs);
                backoffMs *= 2;
            }
        }
    }

    // ---- P23 ---------------------------------------------------------------

    public record UserAction(String type, String targetId, String payload) {
    }

    /** P23: likes and comments made offline, sent as one batch when the network returns. */
    public static final class OfflineActionQueue {
        private final Consumer<List<UserAction>> uploader;
        private final List<UserAction> pending = new ArrayList<>();
        private boolean online;

        public OfflineActionQueue(Consumer<List<UserAction>> uploader) {
            this.uploader = Objects.requireNonNull(uploader, "uploader");
        }

        public void submit(UserAction action) {
            if (online) {
                uploader.accept(Collections.singletonList(action));
            } else {
                pending.add(action);
            }
        }

        public void setOnline(boolean nowOnline) {
            boolean reconnected = nowOnline && !online;
            online = nowOnline;
            if (reconnected && !pending.isEmpty()) {
                uploader.accept(new ArrayList<>(pending));
                pending.clear();
            }
        }

        public int pendingCount() {
            return pending.size();
        }
    }

    // ---- P24 ---------------------------------------------------------------

    public enum SyncDecision { UPDATE_LOCAL, PUSH_LOCAL, IN_SYNC }

    /**
     * P24: a newer server version overwrites the local note; otherwise the local
     * note is pushed. Equal versions are reported as in sync instead of pushing,
     * which would only burn a request and a server-side write.
     */
    public static SyncDecision resolveConflict(long localVersion, long serverVersion) {
        if (serverVersion > localVersion) {
            return SyncDecision.UPDATE_LOCAL;
        }
        return serverVersion == localVersion ? SyncDecision.IN_SYNC : SyncDecision.PUSH_LOCAL;
    }

    // ---- P25 ---------------------------------------------------------------

    public record TransferSpeed(double kilobytesPerSecond, double megabitsPerSecond) {
    }

    /** P25: KB/s uses 1024-byte kilobytes as file managers do; Mbit/s is decimal, as ISPs quote it. */
    public static TransferSpeed downloadSpeed(long bytes, long millis) {
        if (millis <= 0) {
            throw new IllegalArgumentException("elapsed time must be positive");
        }
        double seconds = millis / 1000.0;
        return new TransferSpeed(bytes / 1024.0 / seconds, bytes * 8 / 1_000_000.0 / seconds);
    }

    // ---- P26 ---------------------------------------------------------------

    private static final Pattern LINK_NEXT = Pattern.compile("<([^>]*)>\\s*;\\s*rel=\"?next\"?");
    private static final Pattern PAGE_PARAM = Pattern.compile("[?&]page=(\\d+)");

    /** P26: page number from the {@code rel="next"} entry of an RFC 8288 Link header. */
    public static OptionalInt nextPageFromLinkHeader(String linkHeader) {
        if (linkHeader == null) {
            return OptionalInt.empty();
        }
        Matcher next = LINK_NEXT.matcher(linkHeader);
        if (!next.find()) {
            return OptionalInt.empty();
        }
        Matcher page = PAGE_PARAM.matcher(next.group(1));
        return page.find() ? OptionalInt.of(Integer.parseInt(page.group(1))) : OptionalInt.empty();
    }

    // ---- P27 ---------------------------------------------------------------

    public record HttpResult(int status, byte[] body, String etag) {
    }

    /** P27: conditional GET on the server side; the body loader runs only on a cache miss. */
    public static final class EtagEndpoint {
        private final Supplier<byte[]> bodyLoader;
        private final String currentEtag;

        public EtagEndpoint(String currentEtag, Supplier<byte[]> bodyLoader) {
            this.currentEtag = currentEtag;
            this.bodyLoader = bodyLoader;
        }

        public HttpResult get(String ifNoneMatch) {
            if (currentEtag.equals(ifNoneMatch)) {
                return new HttpResult(304, new byte[0], currentEtag);
            }
            return new HttpResult(200, bodyLoader.get(), currentEtag);
        }
    }

    // ---- P28 ---------------------------------------------------------------

    /** P28: {@code 1024 -> "1.0 KB"}, {@code 1536 -> "1.5 KB"}, {@code 1048576 -> "1.0 MB"}. */
    public static String formatBytes(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("size cannot be negative");
        }
        if (bytes < 1024) {
            return bytes + " B";
        }
        String[] units = {"KB", "MB", "GB", "TB", "PB"};
        double value = bytes;
        int unit = -1;
        while (value >= 1024 && unit < units.length - 1) {
            value /= 1024;
            unit++;
        }
        return String.format(Locale.ROOT, "%.1f %s", value, units[unit]);
    }

    // ---- P29 ---------------------------------------------------------------

    /** P29: listener of a currency quote stream, as a WebSocket client would expose it. */
    public interface QuoteListener {
        void onQuote(String pair, double price, long timestampMs);
    }

    /**
     * P29: WebSocket stand-in emitting a random-walk quote every {@code intervalMs}.
     * The clock is advanced per tick, so the stream stays deterministic for a seed.
     */
    public static final class QuoteFeed {
        private final String pair;
        private final long intervalMs;
        private final Random random;
        private final List<QuoteListener> listeners = new ArrayList<>();
        private double price;
        private long now;

        public QuoteFeed(String pair, double startPrice, long intervalMs, long seed) {
            this.pair = pair;
            this.price = startPrice;
            this.intervalMs = intervalMs;
            this.random = new Random(seed);
        }

        public void subscribe(QuoteListener listener) {
            listeners.add(listener);
        }

        public void emit(int ticks) {
            for (int i = 0; i < ticks; i++) {
                now += intervalMs;
                price = Math.max(0.01, price * (1 + random.nextGaussian() * 0.002));
                for (QuoteListener listener : listeners) {
                    listener.onQuote(pair, price, now);
                }
            }
        }
    }

    // ---- P30 ---------------------------------------------------------------

    /** P30: required profile fields that are missing or {@code null} in the parsed JSON object. */
    public static List<String> missingRequiredFields(Map<String, ?> profileJson, List<String> required) {
        List<String> missing = new ArrayList<>();
        for (String field : required) {
            if (profileJson == null || profileJson.get(field) == null) {
                missing.add(field);
            }
        }
        return missing;
    }

    public static void demo(Report out) {
        out.section("Practice block 3. Networking and sync");
        DeepLink link = parseDeepLink("app://shop/product?id=452&source=push");
        out.item("P21", "host=" + link.host() + ", path=" + link.path() + ", params=" + link.params());
        int[] calls = {0};
        List<Long> waits = new ArrayList<>();
        String retried;
        try {
            retried = withRetry(() -> {
                if (++calls[0] < 3) {
                    throw new SocketTimeoutException("read timed out");
                }
                return "200 OK";
            }, waits::add);
        } catch (Exception e) {
            retried = "failed: " + e;
        }
        out.item("P22", "two timeouts then success -> " + retried + " after " + calls[0] + " calls, backoff " + waits
                + " ms");
        List<String> uploads = new ArrayList<>();
        OfflineActionQueue queue = new OfflineActionQueue(batch -> uploads.add(batch.size() + " actions"));
        queue.submit(new UserAction("like", "post-7", ""));
        queue.submit(new UserAction("comment", "post-7", "Nice!"));
        queue.submit(new UserAction("like", "post-9", ""));
        int queued = queue.pendingCount();
        queue.setOnline(true);
        out.item("P23", "offline queued " + queued + ", reconnect uploads batch " + uploads);
        out.item("P24", "local v3 / server v5 -> " + resolveConflict(3, 5) + ", local v6 / server v5 -> "
                + resolveConflict(6, 5) + ", v5 / v5 -> " + resolveConflict(5, 5));
        TransferSpeed speed = downloadSpeed(52_428_800L, 4_200);
        out.item("P25", String.format(Locale.ROOT, "50 MB in 4.2 s -> %.1f KB/s, %.2f Mbit/s",
                speed.kilobytesPerSecond(), speed.megabitsPerSecond()));
        out.item("P26", "next page = " + nextPageFromLinkHeader(
                "<https://api.com/items?page=1>; rel=\"prev\", <https://api.com/items?page=3>; rel=\"next\"").getAsInt());
        int[] loads = {0};
        EtagEndpoint endpoint = new EtagEndpoint("\"v42\"", () -> {
            loads[0]++;
            return "{\"items\":[]}".getBytes(StandardCharsets.UTF_8);
        });
        int first = endpoint.get(null).status();
        int second = endpoint.get("\"v42\"").status();
        out.item("P27", "first GET -> " + first + ", with If-None-Match -> " + second + ", body loaded " + loads[0]
                + " time");
        out.item("P28", "1024 -> " + formatBytes(1024) + ", 1048576 -> " + formatBytes(1_048_576)
                + ", 1536 -> " + formatBytes(1536));
        QuoteFeed feed = new QuoteFeed("USD/RUB", 92.40, 1000, 11);
        List<String> quotes = new ArrayList<>();
        feed.subscribe((pair, price, ts) -> quotes.add(String.format(Locale.ROOT, "%.2f@%ds", price, ts / 1000)));
        feed.emit(4);
        out.item("P29", "USD/RUB every 1 s: " + quotes);
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", 7);
        profile.put("name", "yorushi");
        profile.put("email", null);
        out.item("P30", "missing required fields "
                + missingRequiredFields(profile, Arrays.asList("id", "name", "email", "avatarUrl")));
    }
}
