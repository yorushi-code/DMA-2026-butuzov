package dev.yorushi.dma.task2.practice;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.Report;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

/** Practical block 1 — authorisation, security and input validation (items 1–10). */
public final class Block1Auth {

    private Block1Auth() {
    }

    // ---- P01 ---------------------------------------------------------------

    /** P01: password rules that failed, empty when the password is strong. */
    public static List<String> passwordViolations(String password) {
        List<String> violations = new ArrayList<>();
        String value = password == null ? "" : password;
        if (value.length() < 8) {
            violations.add("at least 8 characters");
        }
        if (!value.matches(".*[A-Z].*")) {
            violations.add("an uppercase letter");
        }
        if (!value.matches(".*\\d.*")) {
            violations.add("a digit");
        }
        if (!value.matches(".*[!@#$%^&*].*")) {
            violations.add("a special character !@#$%^&*");
        }
        return violations;
    }

    public static boolean isStrongPassword(String password) {
        return passwordViolations(password).isEmpty();
    }

    // ---- P02 ---------------------------------------------------------------

    /**
     * P02: normalises a Russian mobile number to E.164. A domestic leading {@code 8}
     * and a bare 10-digit national number are both rewritten to {@code +7}.
     */
    public static String toE164(String rawPhone) {
        String digits = rawPhone == null ? "" : rawPhone.replaceAll("\\D", "");
        if (digits.length() == 11 && (digits.charAt(0) == '8' || digits.charAt(0) == '7')) {
            return "+7" + digits.substring(1);
        }
        if (digits.length() == 10 && digits.charAt(0) == '9') {
            return "+7" + digits;
        }
        throw new IllegalArgumentException("not a Russian mobile number: " + rawPhone);
    }

    // ---- P03 ---------------------------------------------------------------

    /** P03: six-digit one-time code; zero-padded so every code has the same length. */
    public static String generateOtp(Random random) {
        return String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
    }

    public static String generateOtp() {
        return generateOtp(new SecureRandom());
    }

    // ---- P04 ---------------------------------------------------------------

    /** P04: a JWT is active while its {@code exp} (Unix seconds) is still in the future. */
    public static boolean isTokenActive(long expiresAtEpochSeconds, Clock clock) {
        return clock.instant().getEpochSecond() < expiresAtEpochSeconds;
    }

    // ---- P05 ---------------------------------------------------------------

    /**
     * P05: keeps the first and last character of the local part and masks the rest
     * one-for-one. The assignment's sample shows 11 asterisks for a 14-character
     * hidden segment, which no consistent rule produces, so the length is preserved.
     */
    public static String maskEmail(String email) {
        int at = email == null ? -1 : email.indexOf('@');
        if (at < 1) {
            throw new IllegalArgumentException("not an e-mail address: " + email);
        }
        String local = email.substring(0, at);
        if (local.length() <= 2) {
            return local.charAt(0) + "*" + email.substring(at);
        }
        StringBuilder masked = new StringBuilder(local.length());
        masked.append(local.charAt(0));
        for (int i = 1; i < local.length() - 1; i++) {
            masked.append('*');
        }
        masked.append(local.charAt(local.length() - 1));
        return masked + email.substring(at);
    }

    // ---- P06 ---------------------------------------------------------------

    /** P06: locks password input for 60 seconds after five consecutive failures. */
    public static final class LoginThrottler {
        public static final int MAX_FAILURES = 5;
        public static final Duration LOCKOUT = Duration.ofSeconds(60);

        private final Clock clock;
        private int failures;
        private Instant lockedUntil = Instant.MIN;

        public LoginThrottler(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
        }

        public boolean isLocked() {
            return clock.instant().isBefore(lockedUntil);
        }

        public long secondsRemaining() {
            return isLocked() ? Duration.between(clock.instant(), lockedUntil).getSeconds() : 0;
        }

        /** @return {@code false} when the attempt is rejected because input is locked */
        public boolean recordFailure() {
            if (isLocked()) {
                return false;
            }
            failures++;
            if (failures >= MAX_FAILURES) {
                lockedUntil = clock.instant().plus(LOCKOUT);
                failures = 0;
            }
            return true;
        }

        public void recordSuccess() {
            failures = 0;
        }
    }

    // ---- P07 ---------------------------------------------------------------

    /**
     * P07: columnar transposition. Text is written row by row into {@code key}
     * columns and read column by column; decryption reverses the walk. Works on
     * code points so Cyrillic and emoji survive the round trip.
     */
    public static final class TranspositionCipher {
        private final int key;

        public TranspositionCipher(int key) {
            if (key < 2) {
                throw new IllegalArgumentException("key must be at least 2");
            }
            this.key = key;
        }

        public String encrypt(String plain) {
            int[] cp = plain.codePoints().toArray();
            StringBuilder out = new StringBuilder(plain.length());
            for (int column = 0; column < key; column++) {
                for (int i = column; i < cp.length; i += key) {
                    out.appendCodePoint(cp[i]);
                }
            }
            return out.toString();
        }

        public String decrypt(String cipher) {
            int[] cp = cipher.codePoints().toArray();
            int[] plain = new int[cp.length];
            int read = 0;
            for (int column = 0; column < key; column++) {
                for (int i = column; i < cp.length; i += key) {
                    plain[i] = cp[read++];
                }
            }
            return new String(plain, 0, plain.length);
        }
    }

    // ---- P08 ---------------------------------------------------------------

    public enum BiometricStatus { READY, NO_HARDWARE, NO_PERMISSION, NO_SECURE_LOCK, NOT_ENROLLED }

    /**
     * P08: mirrors the order in which {@code BiometricManager} reports problems:
     * hardware first, then what the user can fix.
     */
    public static BiometricStatus biometricReadiness(boolean hasSensor, boolean permissionGranted,
            boolean deviceSecure, boolean fingerprintEnrolled) {
        if (!hasSensor) {
            return BiometricStatus.NO_HARDWARE;
        }
        if (!permissionGranted) {
            return BiometricStatus.NO_PERMISSION;
        }
        if (!deviceSecure) {
            return BiometricStatus.NO_SECURE_LOCK;
        }
        return fingerprintEnrolled ? BiometricStatus.READY : BiometricStatus.NOT_ENROLLED;
    }

    // ---- P09 ---------------------------------------------------------------

    /** P09: resets the screen state after three minutes without user interaction. */
    public static final class InactivityTimeout {
        public static final Duration IDLE_LIMIT = Duration.ofMinutes(3);

        private final Clock clock;
        private final Runnable onReset;
        private Instant lastInteraction;

        public InactivityTimeout(Clock clock, Runnable onReset) {
            this.clock = Objects.requireNonNull(clock, "clock");
            this.onReset = Objects.requireNonNull(onReset, "onReset");
            this.lastInteraction = clock.instant();
        }

        public void onUserInteraction() {
            lastInteraction = clock.instant();
        }

        /** Called periodically (e.g. from {@code onResume} or a ticker); resets when idle too long. */
        public boolean checkAndReset() {
            if (Duration.between(lastInteraction, clock.instant()).compareTo(IDLE_LIMIT) > 0) {
                onReset.run();
                lastInteraction = clock.instant();
                return true;
            }
            return false;
        }
    }

    // ---- P10 ---------------------------------------------------------------

    private static final Pattern PROMO = Pattern.compile("[A-Z]{4}-\\d{4}");

    /** P10: four uppercase Latin letters, a hyphen and four digits, e.g. {@code SALE-2026}. */
    public static boolean isValidPromoCode(String code) {
        return code != null && PROMO.matcher(code).matches();
    }

    public static void demo(Report out) {
        out.section("Practice block 1. Authorisation and validation");
        out.item("P01", "\"qwerty\" misses " + passwordViolations("qwerty")
                + "; \"S3cure!pass\" strong=" + isStrongPassword("S3cure!pass"));
        out.item("P02", "8 (999) 000-11-22 -> " + toE164("8 (999) 000-11-22")
                + ", +7 999 000 11 22 -> " + toE164("+7 999 000 11 22"));
        out.item("P03", "OTP (seeded) = " + generateOtp(new Random(7)) + ", OTP (secure) is "
                + generateOtp().length() + " digits");
        MutableClock clock = new MutableClock(Instant.ofEpochSecond(1_790_000_000L));
        out.item("P04", "exp=now+600 active=" + isTokenActive(1_790_000_600L, clock)
                + ", exp=now-1 active=" + isTokenActive(1_789_999_999L, clock));
        out.item("P05", "alexander.ivanov@mail.ru -> " + maskEmail("alexander.ivanov@mail.ru"));
        LoginThrottler throttler = new LoginThrottler(clock);
        for (int i = 0; i < LoginThrottler.MAX_FAILURES; i++) {
            throttler.recordFailure();
        }
        boolean lockedNow = throttler.isLocked();
        long remaining = throttler.secondsRemaining();
        clock.advance(Duration.ofSeconds(61));
        out.item("P06", "after 5 failures locked=" + lockedNow + " (" + remaining + " s left), after 61 s locked="
                + throttler.isLocked());
        TranspositionCipher cipher = new TranspositionCipher(4);
        // The emoji sits outside the BMP, so it exercises the code-point handling.
        String draft = "Draft: buy milk \u2615\uD83E\uDD5B";
        String secret = cipher.encrypt(draft);
        out.item("P07", "\"" + draft + "\" -> \"" + secret + "\" -> \"" + cipher.decrypt(secret) + "\"");
        out.item("P08", "sensor+permission+lock+enrolled -> " + biometricReadiness(true, true, true, true)
                + ", no enrolled finger -> " + biometricReadiness(true, true, true, false)
                + ", no sensor -> " + biometricReadiness(false, true, true, true));
        List<String> resets = new ArrayList<>();
        InactivityTimeout timeout = new InactivityTimeout(clock, () -> resets.add("reset"));
        clock.advance(Duration.ofMinutes(2));
        boolean after2 = timeout.checkAndReset();
        clock.advance(Duration.ofMinutes(2));
        boolean after4 = timeout.checkAndReset();
        out.item("P09", "idle 2 min reset=" + after2 + ", idle 4 min reset=" + after4 + " " + resets);
        out.item("P10", "SALE-2026=" + isValidPromoCode("SALE-2026") + ", sale-2026=" + isValidPromoCode("sale-2026")
                + ", SALE2026=" + isValidPromoCode("SALE2026"));
    }
}
