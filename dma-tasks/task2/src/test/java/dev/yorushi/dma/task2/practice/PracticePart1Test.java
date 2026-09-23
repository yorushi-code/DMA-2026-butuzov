package dev.yorushi.dma.task2.practice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.practice.Block1Auth.BiometricStatus;
import dev.yorushi.dma.task2.practice.Block1Auth.InactivityTimeout;
import dev.yorushi.dma.task2.practice.Block1Auth.LoginThrottler;
import dev.yorushi.dma.task2.practice.Block1Auth.TranspositionCipher;
import dev.yorushi.dma.task2.practice.Block2Lists.CartLine;
import dev.yorushi.dma.task2.practice.Block2Lists.CatalogItem;
import dev.yorushi.dma.task2.practice.Block2Lists.Chat;
import dev.yorushi.dma.task2.practice.Block2Lists.GalleryFile;
import dev.yorushi.dma.task2.practice.Block2Lists.ImageCache;
import dev.yorushi.dma.task2.practice.Block2Lists.NewsDiff;
import dev.yorushi.dma.task2.practice.Block2Lists.NewsItem;
import dev.yorushi.dma.task2.practice.Block2Lists.PaginationHelper;
import dev.yorushi.dma.task2.practice.Block2Lists.UndoBuffer;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Practical items 1–20 of task 2. */
class PracticePart1Test {

    // ---- Block 1 -------------------------------------------------------------

    @Test
    @DisplayName("P01 password strength rules")
    void p01() {
        assertTrue(Block1Auth.isStrongPassword("S3cure!pass"));
        assertEquals(Collections.singletonList("a special character !@#$%^&*"),
                Block1Auth.passwordViolations("S3curepass"));
        assertEquals(4, Block1Auth.passwordViolations("").size());
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"8 (999) 000-11-22", "+7 999 000 11 22", "89990001122", "9990001122", "7-999-000-11-22"})
    @DisplayName("P02 phone numbers normalised to E.164")
    void p02(String raw) {
        assertEquals("+79990001122", Block1Auth.toE164(raw));
    }

    @Test
    @DisplayName("P02 rejects numbers that are not Russian mobiles")
    void p02Invalid() {
        assertThrows(IllegalArgumentException.class, () -> Block1Auth.toE164("12345"));
    }

    @Test
    @DisplayName("P03 OTP is always six digits, including leading zeros")
    void p03() {
        Random random = new Random(1);
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String code = Block1Auth.generateOtp(random);
            assertTrue(code.matches("\\d{6}"), code);
            codes.add(code);
        }
        assertTrue(codes.size() > 990, "codes should be well spread");
        assertEquals("000042", Block1Auth.generateOtp(new Random() {
            @Override
            public int nextInt(int bound) {
                return 42;
            }
        }));
    }

    @Test
    @DisplayName("P04 JWT activity against the current time")
    void p04() {
        MutableClock clock = new MutableClock(Instant.ofEpochSecond(1000));
        assertTrue(Block1Auth.isTokenActive(1001, clock));
        assertFalse(Block1Auth.isTokenActive(1000, clock));
    }

    @Test
    @DisplayName("P05 e-mail masking keeps first and last character")
    void p05() {
        assertEquals("a**************v@mail.ru", Block1Auth.maskEmail("alexander.ivanov@mail.ru"));
        assertEquals("a*@x.io", Block1Auth.maskEmail("ab@x.io"));
        assertThrows(IllegalArgumentException.class, () -> Block1Auth.maskEmail("no-at-sign"));
    }

    @Test
    @DisplayName("P06 LoginThrottler locks for 60 s after 5 failures")
    void p06() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        LoginThrottler throttler = new LoginThrottler(clock);
        for (int i = 0; i < 4; i++) {
            throttler.recordFailure();
        }
        assertFalse(throttler.isLocked());
        throttler.recordFailure();
        assertTrue(throttler.isLocked());
        assertEquals(60, throttler.secondsRemaining());
        assertFalse(throttler.recordFailure(), "attempts are rejected while locked");
        clock.advance(Duration.ofSeconds(60));
        assertFalse(throttler.isLocked());
    }

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {"", "a", "Draft note", "Emoji 😀 inside", "exactly12chr"})
    @DisplayName("P07 transposition cipher round-trips")
    void p07(String text) {
        TranspositionCipher cipher = new TranspositionCipher(3);
        assertEquals(text, cipher.decrypt(cipher.encrypt(text)));
    }

    @Test
    @DisplayName("P07 cipher actually permutes the text")
    void p07Permutes() {
        assertEquals("adbecf", new TranspositionCipher(3).encrypt("abcdef"));
    }

    @Test
    @DisplayName("P08 biometric readiness checks hardware before user-fixable issues")
    void p08() {
        assertEquals(BiometricStatus.READY, Block1Auth.biometricReadiness(true, true, true, true));
        assertEquals(BiometricStatus.NO_HARDWARE, Block1Auth.biometricReadiness(false, false, false, false));
        assertEquals(BiometricStatus.NO_PERMISSION, Block1Auth.biometricReadiness(true, false, true, true));
        assertEquals(BiometricStatus.NO_SECURE_LOCK, Block1Auth.biometricReadiness(true, true, false, true));
        assertEquals(BiometricStatus.NOT_ENROLLED, Block1Auth.biometricReadiness(true, true, true, false));
    }

    @Test
    @DisplayName("P09 screen state resets after 3 minutes of inactivity")
    void p09() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        int[] resets = {0};
        InactivityTimeout timeout = new InactivityTimeout(clock, () -> resets[0]++);
        clock.advance(Duration.ofMinutes(2));
        timeout.onUserInteraction();
        clock.advance(Duration.ofMinutes(2));
        assertFalse(timeout.checkAndReset());
        clock.advance(Duration.ofMinutes(2));
        assertTrue(timeout.checkAndReset());
        assertEquals(1, resets[0]);
    }

    @Test
    @DisplayName("P10 promo code mask XXXX-9999")
    void p10() {
        assertTrue(Block1Auth.isValidPromoCode("SALE-2026"));
        assertFalse(Block1Auth.isValidPromoCode("SAL-2026"));
        assertFalse(Block1Auth.isValidPromoCode("SALE-202"));
        assertFalse(Block1Auth.isValidPromoCode("Sale-2026"));
        assertFalse(Block1Auth.isValidPromoCode(null));
    }

    // ---- Block 2 -------------------------------------------------------------

    @Test
    @DisplayName("P11 diff reports changed and removed ids")
    void p11() {
        NewsDiff diff = Block2Lists.diffNews(
                Arrays.asList(new NewsItem(1, "a", 1), new NewsItem(2, "b", 1), new NewsItem(3, "c", 1)),
                Arrays.asList(new NewsItem(1, "a", 1), new NewsItem(3, "c", 2)));
        assertEquals(Collections.singletonList(3L), diff.changedIds());
        assertEquals(Collections.singletonList(2L), diff.removedIds());
    }

    @Test
    @DisplayName("P12 pagination with pageSize 20")
    void p12() {
        List<Integer> all = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            all.add(i);
        }
        PaginationHelper helper = new PaginationHelper();
        assertEquals(20, helper.page(all, 1).size());
        assertEquals(Integer.valueOf(20), helper.page(all, 2).get(0));
        assertEquals(10, helper.page(all, 3).size());
        assertTrue(helper.page(all, 4).isEmpty());
        assertEquals(3, helper.pageCount(all.size()));
        assertThrows(IllegalArgumentException.class, () -> helper.page(all, 0));
    }

    @Test
    @DisplayName("P13 contacts grouped by first letter")
    void p13() {
        Map<Character, List<String>> groups = Block2Lists.groupByFirstLetter(
                Arrays.asList("bob", "Alice", "anna", " ", "Bert"));
        assertEquals(Arrays.asList('A', 'B'), new ArrayList<>(groups.keySet()));
        assertEquals(Arrays.asList("Alice", "anna"), groups.get('A'));
        assertEquals(Arrays.asList("Bert", "bob"), groups.get('B'));
    }

    @Test
    @DisplayName("P14 catalogue filter by name or article, case-insensitive")
    void p14() {
        List<CatalogItem> catalog = Arrays.asList(new CatalogItem("AB-1", "Red Mug"),
                new CatalogItem("CD-2", "Blue Cup"));
        assertEquals(1, Block2Lists.filterCatalog(catalog, "MUG").size());
        assertEquals("Blue Cup", Block2Lists.filterCatalog(catalog, "cd-").get(0).name());
        assertEquals(2, Block2Lists.filterCatalog(catalog, "  ").size());
    }

    @Test
    @DisplayName("P15 banner carousel wraps around")
    void p15() {
        assertEquals(1, Block2Lists.nextBanner(0, 3));
        assertEquals(0, Block2Lists.nextBanner(2, 3));
        assertEquals(0, Block2Lists.nextBanner(-1, 3));
    }

    @Test
    @DisplayName("P16 cart total with per-category promo discounts")
    void p16() {
        List<CartLine> lines = Arrays.asList(
                new CartLine("Phone", "electronics", new BigDecimal("10000.00"), 1),
                new CartLine("Book", "books", new BigDecimal("333.33"), 3));
        Map<String, Integer> promo = Collections.singletonMap("electronics", 15);
        assertEquals(new BigDecimal("9499.99"), Block2Lists.cartTotal(lines, promo));
        assertEquals(new BigDecimal("10999.99"), Block2Lists.cartTotal(lines, Collections.emptyMap()));
    }

    @Test
    @DisplayName("P17 swipe-to-delete with undo and delayed commit")
    void p17() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        UndoBuffer<String> buffer = new UndoBuffer<>(list, clock);
        buffer.swipeAway(1);
        assertEquals(Arrays.asList("a", "c"), list);
        assertTrue(buffer.undo());
        assertEquals(Arrays.asList("a", "b", "c"), list);
        buffer.swipeAway(2);
        clock.advance(Duration.ofSeconds(3));
        assertFalse(buffer.tick());
        clock.advance(Duration.ofSeconds(1));
        assertTrue(buffer.tick());
        assertFalse(buffer.undo());
        assertEquals(Collections.singletonList("c"), buffer.committed());
    }

    @Test
    @DisplayName("P18 chats sorted by the last message, newest first")
    void p18() {
        List<Chat> sorted = Block2Lists.sortChats(Arrays.asList(new Chat("old", 1), new Chat("new", 9),
                new Chat("mid", 5)));
        assertEquals("new", sorted.get(0).title());
        assertEquals("old", sorted.get(2).title());
    }

    @Test
    @DisplayName("P19 duplicate files found by name and size")
    void p19() {
        List<List<GalleryFile>> groups = Block2Lists.findDuplicates(Arrays.asList(
                new GalleryFile("/a/x.jpg", "x.jpg", 10), new GalleryFile("/b/X.JPG", "X.JPG", 10),
                new GalleryFile("/c/x.jpg", "x.jpg", 11), new GalleryFile("/d/y.jpg", "y.jpg", 10)));
        assertEquals(1, groups.size());
        assertEquals(2, groups.get(0).size());
    }

    @Test
    @DisplayName("P20 image cache evicts the oldest file above 100 MB")
    void p20() {
        ImageCache cache = new ImageCache();
        assertTrue(cache.put("1", 60L << 20).isEmpty());
        assertTrue(cache.put("2", 40L << 20).isEmpty());
        assertEquals(Collections.singletonList("1"), cache.put("3", 1));
        assertEquals(Arrays.asList("2", "3"), cache.keys());
        assertTrue(cache.totalBytes() <= ImageCache.DEFAULT_LIMIT_BYTES);
    }
}
