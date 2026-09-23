package dev.yorushi.dma.task2.practice;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.Report;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Practical block 2 — lists, catalogues and caches (items 11–20). */
public final class Block2Lists {

    private Block2Lists() {
    }

    // ---- P11 ---------------------------------------------------------------

    public record NewsItem(long id, String title, long updatedAt) {
    }

    /** Changed and removed positions, the part of a DiffUtil result the adapter needs. */
    public record NewsDiff(List<Long> changedIds, List<Long> removedIds) {
    }

    /** P11: ids whose content changed, and ids that disappeared from the new list. */
    public static NewsDiff diffNews(List<NewsItem> oldList, List<NewsItem> newList) {
        Map<Long, NewsItem> fresh = new HashMap<>();
        for (NewsItem item : newList) {
            fresh.put(item.id(), item);
        }
        List<Long> changed = new ArrayList<>();
        List<Long> removed = new ArrayList<>();
        for (NewsItem old : oldList) {
            NewsItem updated = fresh.get(old.id());
            if (updated == null) {
                removed.add(old.id());
            } else if (!updated.equals(old)) {
                changed.add(old.id());
            }
        }
        return new NewsDiff(changed, removed);
    }

    // ---- P12 ---------------------------------------------------------------

    /** P12: slices a feed into pages; pages are 1-based as shown to the user. */
    public static final class PaginationHelper {
        public static final int DEFAULT_PAGE_SIZE = 20;

        private final int pageSize;

        public PaginationHelper() {
            this(DEFAULT_PAGE_SIZE);
        }

        public PaginationHelper(int pageSize) {
            if (pageSize < 1) {
                throw new IllegalArgumentException("page size must be positive");
            }
            this.pageSize = pageSize;
        }

        public <T> List<T> page(List<T> all, int page) {
            if (page < 1) {
                throw new IllegalArgumentException("pages start at 1: " + page);
            }
            int from = (page - 1) * pageSize;
            if (from >= all.size()) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(all.subList(from, Math.min(from + pageSize, all.size())));
        }

        public int pageCount(int totalItems) {
            return (totalItems + pageSize - 1) / pageSize;
        }
    }

    // ---- P13 ---------------------------------------------------------------

    /** P13: contacts grouped under their upper-cased first letter, both levels sorted. */
    public static Map<Character, List<String>> groupByFirstLetter(List<String> names) {
        Map<Character, List<String>> sections = new TreeMap<>();
        for (String name : names) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            char letter = Character.toUpperCase(trimmed.charAt(0));
            sections.computeIfAbsent(letter, key -> new ArrayList<>()).add(trimmed);
        }
        for (List<String> section : sections.values()) {
            section.sort(String.CASE_INSENSITIVE_ORDER);
        }
        return sections;
    }

    // ---- P14 ---------------------------------------------------------------

    public record CatalogItem(String sku, String name) {
    }

    /** P14: case-insensitive substring match against the name or the article number. */
    public static List<CatalogItem> filterCatalog(List<CatalogItem> catalog, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            return new ArrayList<>(catalog);
        }
        List<CatalogItem> matches = new ArrayList<>();
        for (CatalogItem item : catalog) {
            if (item.name().toLowerCase(Locale.ROOT).contains(needle)
                    || item.sku().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(item);
            }
        }
        return matches;
    }

    // ---- P15 ---------------------------------------------------------------

    /** P15: next banner index, wrapping from the last banner back to the first. */
    public static int nextBanner(int currentIndex, int bannerCount) {
        if (bannerCount < 1) {
            throw new IllegalArgumentException("carousel is empty");
        }
        return Math.floorMod(currentIndex + 1, bannerCount);
    }

    // ---- P16 ---------------------------------------------------------------

    public record CartLine(String title, String category, BigDecimal unitPrice, int quantity) {
    }

    /**
     * P16: cart total where a promo code grants per-category discounts. Money uses
     * {@link BigDecimal}; binary floating point cannot represent kopecks exactly.
     *
     * @param categoryDiscounts category → discount percent granted by the promo code
     */
    public static BigDecimal cartTotal(List<CartLine> lines, Map<String, Integer> categoryDiscounts) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartLine line : lines) {
            BigDecimal lineTotal = line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()));
            int percent = categoryDiscounts.getOrDefault(line.category(), 0);
            BigDecimal factor = BigDecimal.valueOf(100 - percent).movePointLeft(2);
            total = total.add(lineTotal.multiply(factor));
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // ---- P17 ---------------------------------------------------------------

    /**
     * P17: swipe-to-delete buffer. The item disappears from the list at once but
     * is only committed after the undo window; undo puts it back at its position.
     */
    public static final class UndoBuffer<T> {
        public static final Duration UNDO_WINDOW = Duration.ofMillis(3500);

        private final List<T> items;
        private final Clock clock;
        private final List<T> committed = new ArrayList<>();
        private T pending;
        private int pendingIndex = -1;
        private Instant deadline;

        public UndoBuffer(List<T> items, Clock clock) {
            this.items = items;
            this.clock = Objects.requireNonNull(clock, "clock");
        }

        public void swipeAway(int index) {
            commitPending();
            pending = items.remove(index);
            pendingIndex = index;
            deadline = clock.instant().plus(UNDO_WINDOW);
        }

        public boolean undo() {
            if (pending == null) {
                return false;
            }
            items.add(pendingIndex, pending);
            pending = null;
            return true;
        }

        /** Commits the deletion once the undo window has passed; call from a timer. */
        public boolean tick() {
            if (pending != null && !clock.instant().isBefore(deadline)) {
                commitPending();
                return true;
            }
            return false;
        }

        private void commitPending() {
            if (pending != null) {
                committed.add(pending);
                pending = null;
            }
        }

        public List<T> committed() {
            return Collections.unmodifiableList(committed);
        }
    }

    // ---- P18 ---------------------------------------------------------------

    public record Chat(String title, long lastMessageAt) {
    }

    /** P18: freshest conversation first. */
    public static List<Chat> sortChats(List<Chat> chats) {
        List<Chat> sorted = new ArrayList<>(chats);
        sorted.sort(Comparator.comparingLong(Chat::lastMessageAt).reversed());
        return sorted;
    }

    // ---- P19 ---------------------------------------------------------------

    public record GalleryFile(String path, String name, long sizeBytes) {
    }

    /**
     * P19: files sharing name and size, grouped. Matching on these two first is the
     * cheap pre-filter; only candidate groups would then be hashed for a checksum.
     */
    public static List<List<GalleryFile>> findDuplicates(List<GalleryFile> files) {
        Map<String, List<GalleryFile>> byKey = new LinkedHashMap<>();
        for (GalleryFile file : files) {
            String key = file.name().toLowerCase(Locale.ROOT) + '|' + file.sizeBytes();
            byKey.computeIfAbsent(key, k -> new ArrayList<>()).add(file);
        }
        List<List<GalleryFile>> duplicates = new ArrayList<>();
        for (List<GalleryFile> group : byKey.values()) {
            if (group.size() > 1) {
                duplicates.add(group);
            }
        }
        return duplicates;
    }

    // ---- P20 ---------------------------------------------------------------

    /** P20: image cache evicting the oldest files (FIFO) once it exceeds its byte budget. */
    public static final class ImageCache {
        public static final long DEFAULT_LIMIT_BYTES = 100L * 1024 * 1024;

        private final long limitBytes;
        private final LinkedHashMap<String, Long> files = new LinkedHashMap<>();
        private long totalBytes;

        public ImageCache() {
            this(DEFAULT_LIMIT_BYTES);
        }

        public ImageCache(long limitBytes) {
            this.limitBytes = limitBytes;
        }

        /** @return keys evicted to make room */
        public List<String> put(String key, long sizeBytes) {
            Long previous = files.remove(key);
            if (previous != null) {
                totalBytes -= previous;
            }
            files.put(key, sizeBytes);
            totalBytes += sizeBytes;
            List<String> evicted = new ArrayList<>();
            Iterator<Map.Entry<String, Long>> oldestFirst = files.entrySet().iterator();
            while (totalBytes > limitBytes && oldestFirst.hasNext()) {
                Map.Entry<String, Long> oldest = oldestFirst.next();
                totalBytes -= oldest.getValue();
                evicted.add(oldest.getKey());
                oldestFirst.remove();
            }
            return evicted;
        }

        public long totalBytes() {
            return totalBytes;
        }

        public List<String> keys() {
            return new ArrayList<>(files.keySet());
        }
    }

    public static void demo(Report out) {
        out.section("Practice block 2. Lists, catalogues and caches");
        NewsDiff diff = diffNews(
                Arrays.asList(new NewsItem(1, "A", 10), new NewsItem(2, "B", 10), new NewsItem(3, "C", 10)),
                Arrays.asList(new NewsItem(1, "A", 10), new NewsItem(3, "C (upd)", 20), new NewsItem(4, "D", 5)));
        out.item("P11", "changed " + diff.changedIds() + ", removed " + diff.removedIds());
        List<Integer> feed = new ArrayList<>();
        for (int i = 1; i <= 45; i++) {
            feed.add(i);
        }
        PaginationHelper pages = new PaginationHelper();
        List<Integer> third = pages.page(feed, 3);
        out.item("P12", "45 news, pageSize 20: pages=" + pages.pageCount(feed.size()) + ", page 3 = " + third
                + ", page 4 = " + pages.page(feed, 4));
        out.item("P13", groupByFirstLetter(Arrays.asList("mila", "Anna", "Maxim", "alex", "Boris")).toString());
        List<CatalogItem> catalog = Arrays.asList(new CatalogItem("AB-100", "Wireless Mouse"),
                new CatalogItem("KB-220", "Mechanical Keyboard"), new CatalogItem("MS-330", "Mouse Pad XL"));
        out.item("P14", "\"mouse\" -> " + filterCatalog(catalog, "mouse").size() + " items, \"kb-2\" -> "
                + filterCatalog(catalog, "kb-2").get(0).name());
        out.item("P15", "5 banners: after 3 -> " + nextBanner(3, 5) + ", after 4 -> " + nextBanner(4, 5));
        Map<String, Integer> promo = new HashMap<>();
        promo.put("electronics", 10);
        promo.put("books", 25);
        BigDecimal total = cartTotal(Arrays.asList(
                new CartLine("Headphones", "electronics", new BigDecimal("4990.00"), 1),
                new CartLine("Novel", "books", new BigDecimal("650.00"), 2),
                new CartLine("Mug", "home", new BigDecimal("399.90"), 1)), promo);
        out.item("P16", "electronics -10%, books -25% -> total " + total);
        MutableClock clock = new MutableClock(Instant.parse("2026-09-23T12:00:00Z"));
        List<String> inbox = new ArrayList<>(Arrays.asList("mail-1", "mail-2", "mail-3"));
        UndoBuffer<String> undo = new UndoBuffer<>(inbox, clock);
        undo.swipeAway(1);
        String afterSwipe = inbox.toString();
        undo.undo();
        String afterUndo = inbox.toString();
        undo.swipeAway(0);
        clock.advance(Duration.ofSeconds(4));
        undo.tick();
        out.item("P17", "swipe mail-2 -> " + afterSwipe + ", undo -> " + afterUndo
                + ", swipe mail-1 + 4 s -> committed " + undo.committed());
        List<Chat> chats = sortChats(Arrays.asList(new Chat("Work", 1000), new Chat("Mom", 3000),
                new Chat("Shop", 2000)));
        StringBuilder chatOrder = new StringBuilder();
        for (Chat chat : chats) {
            chatOrder.append(chat.title()).append(' ');
        }
        out.item("P18", "freshest first: " + chatOrder.toString().trim());
        List<List<GalleryFile>> dup = findDuplicates(Arrays.asList(
                new GalleryFile("/DCIM/IMG_1.jpg", "IMG_1.jpg", 2_048_000),
                new GalleryFile("/Download/IMG_1.jpg", "IMG_1.jpg", 2_048_000),
                new GalleryFile("/DCIM/IMG_2.jpg", "IMG_2.jpg", 1_024_000)));
        out.item("P19", dup.size() + " duplicate group: " + dup.get(0).get(0).path() + " = " + dup.get(0).get(1).path());
        ImageCache cache = new ImageCache();
        cache.put("a.webp", 40L << 20);
        cache.put("b.webp", 40L << 20);
        List<String> evicted = cache.put("c.webp", 30L << 20);
        out.item("P20", "40 + 40 + 30 MB with 100 MB limit -> evicted " + evicted + ", kept " + cache.keys()
                + " (" + (cache.totalBytes() >> 20) + " MB)");
    }
}
