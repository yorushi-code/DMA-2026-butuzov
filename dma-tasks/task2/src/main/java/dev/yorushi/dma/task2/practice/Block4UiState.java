package dev.yorushi.dma.task2.practice;

import dev.yorushi.dma.task2.MutableClock;
import dev.yorushi.dma.task2.Report;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLException;

/** Practical block 4 — screen state and UI architecture (items 31–40). */
public final class Block4UiState {

    private Block4UiState() {
    }

    // ---- P31 ---------------------------------------------------------------

    /** P31: MVI screen state; the sealed hierarchy makes the set of states closed. */
    public sealed interface UiState<T> permits Loading, Success, Empty, Error {
    }

    public record Loading<T>() implements UiState<T> {
    }

    public record Success<T>(List<T> data) implements UiState<T> {
    }

    public record Empty<T>() implements UiState<T> {
    }

    public record Error<T>(String message) implements UiState<T> {
    }

    /** Events that drive the loading screen. */
    public interface LoadEvent<T> {
    }

    public record Started<T>() implements LoadEvent<T> {
    }

    public record Loaded<T>(List<T> items) implements LoadEvent<T> {
    }

    public record Failed<T>(String reason) implements LoadEvent<T> {
    }

    /** P31: pure reducer — the next state depends only on the event. */
    public static <T> UiState<T> reduce(LoadEvent<T> event) {
        if (event instanceof Started) {
            return new Loading<>();
        }
        if (event instanceof Loaded<T> loaded) {
            return loaded.items().isEmpty() ? new Empty<>() : new Success<>(loaded.items());
        }
        if (event instanceof Failed<T> failed) {
            return new Error<>(failed.reason());
        }
        throw new IllegalArgumentException("unknown event " + event);
    }

    public static String render(UiState<?> state) {
        if (state instanceof Loading) {
            return "spinner";
        }
        if (state instanceof Success<?> success) {
            return "list of " + success.data().size();
        }
        if (state instanceof Empty) {
            return "empty placeholder";
        }
        return "error banner: " + ((Error<?>) state).message();
    }

    // ---- P32 ---------------------------------------------------------------

    /** P32: ignores repeat clicks that arrive within 500 ms of the last accepted one. */
    public static final class ClickDebouncer {
        public static final Duration WINDOW = Duration.ofMillis(500);

        private final Clock clock;
        private Instant lastAccepted;

        public ClickDebouncer(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
        }

        /** @return whether the action ran */
        public boolean onClick(Runnable action) {
            Instant now = clock.instant();
            if (lastAccepted != null && Duration.between(lastAccepted, now).compareTo(WINDOW) < 0) {
                return false;
            }
            lastAccepted = now;
            action.run();
            return true;
        }
    }

    // ---- P33 ---------------------------------------------------------------

    /** P33: navigation back stack built on its own linked nodes rather than a JDK collection. */
    public static final class BackStack<S> {
        private static final class Node<S> {
            final S screen;
            final Node<S> below;

            Node(S screen, Node<S> below) {
                this.screen = screen;
                this.below = below;
            }
        }

        private Node<S> top;
        private int size;

        public void push(S screen) {
            top = new Node<>(Objects.requireNonNull(screen, "screen"), top);
            size++;
        }

        public S pop() {
            if (top == null) {
                throw new NoSuchElementException("back stack is empty");
            }
            S screen = top.screen;
            top = top.below;
            size--;
            return screen;
        }

        /** Pops everything above the root; the root screen itself stays. */
        public void popToRoot() {
            while (size > 1) {
                pop();
            }
        }

        public S peek() {
            return top == null ? null : top.screen;
        }

        public int size() {
            return size;
        }

        public List<S> toListRootFirst() {
            List<S> screens = new ArrayList<>(size);
            for (Node<S> n = top; n != null; n = n.below) {
                screens.add(n.screen);
            }
            Collections.reverse(screens);
            return screens;
        }
    }

    // ---- P34 ---------------------------------------------------------------

    public enum ThemeMode { LIGHT, DARK }

    public enum ColorRole { BACKGROUND, SURFACE, PRIMARY, ON_BACKGROUND, ERROR }

    /** P34: HEX colours per role for the selected appearance. */
    public static final class ThemePalette {
        private final Map<ColorRole, String> colors = new EnumMap<>(ColorRole.class);

        public ThemePalette(ThemeMode mode) {
            boolean dark = mode == ThemeMode.DARK;
            colors.put(ColorRole.BACKGROUND, dark ? "#121212" : "#FFFFFF");
            colors.put(ColorRole.SURFACE, dark ? "#1E1E1E" : "#F5F5F5");
            colors.put(ColorRole.PRIMARY, dark ? "#90CAF9" : "#1976D2");
            colors.put(ColorRole.ON_BACKGROUND, dark ? "#E0E0E0" : "#212121");
            colors.put(ColorRole.ERROR, dark ? "#CF6679" : "#B00020");
        }

        public String hex(ColorRole role) {
            return colors.get(role);
        }
    }

    // ---- P35 ---------------------------------------------------------------

    public record Profile(String avatarUrl, String bio, String phone, String email, String city) {
    }

    /** P35: share of the five profile fields that are filled in, as a whole percent. */
    public static int profileCompleteness(Profile profile) {
        String[] fields = {profile.avatarUrl(), profile.bio(), profile.phone(), profile.email(), profile.city()};
        int filled = 0;
        for (String field : fields) {
            if (field != null && !field.trim().isEmpty()) {
                filled++;
            }
        }
        return filled * 100 / fields.length;
    }

    // ---- P36 ---------------------------------------------------------------

    public enum TextColor { BLACK, WHITE }

    /** P36: YIQ brightness; at 128 and above the background is light enough for black text. */
    public static TextColor readableTextOn(int red, int green, int blue) {
        int yiq = (red * 299 + green * 587 + blue * 114) / 1000;
        return yiq >= 128 ? TextColor.BLACK : TextColor.WHITE;
    }

    // ---- P37 ---------------------------------------------------------------

    /**
     * P37: {@code 950 -> "950"}, {@code 1200 -> "1.2K"}, {@code 1500000 -> "1.5M"}.
     * Rounds down, as social counters do, so 1999 never inflates to "2K".
     */
    public static String formatCount(long count) {
        long absolute = Math.abs(count);
        if (absolute < 1000) {
            return Long.toString(count);
        }
        String[] suffixes = {"K", "M", "B"};
        long scale = 1000;
        int index = 0;
        while (index < suffixes.length - 1 && absolute >= scale * 1000) {
            scale *= 1000;
            index++;
        }
        long tenths = absolute * 10 / scale;
        String number = tenths % 10 == 0 ? Long.toString(tenths / 10) : (tenths / 10) + "." + (tenths % 10);
        return (count < 0 ? "-" : "") + number + suffixes[index];
    }

    // ---- P38 ---------------------------------------------------------------

    /** P38: shows one dialog at a time; later requests wait until the current one is dismissed. */
    public static final class DialogQueue {
        private final Queue<String> waiting = new ArrayDeque<>();
        private final List<String> shownInOrder = new ArrayList<>();
        private String visible;

        public void show(String dialog) {
            if (visible == null) {
                display(dialog);
            } else {
                waiting.offer(dialog);
            }
        }

        public void dismiss() {
            visible = null;
            String next = waiting.poll();
            if (next != null) {
                display(next);
            }
        }

        private void display(String dialog) {
            visible = dialog;
            shownInOrder.add(dialog);
        }

        public String visible() {
            return visible;
        }

        public List<String> shownInOrder() {
            return shownInOrder;
        }
    }

    // ---- P39 ---------------------------------------------------------------

    /** P39: the "Pay" button is enabled only when all three preconditions hold. */
    public static boolean isPayButtonEnabled(int cartItems, boolean paymentMethodSelected, boolean addressConfirmed) {
        return cartItems > 0 && paymentMethodSelected && addressConfirmed;
    }

    // ---- P40 ---------------------------------------------------------------

    /**
     * P40: user-facing hint for a technical failure. Messages live in a
     * {@link ResourceBundle}, so the Russian wording required by the assignment
     * sits in {@code ErrorMessages_ru.properties} next to the English default.
     */
    public static String translateError(Throwable error, Locale locale) {
        ResourceBundle messages = ResourceBundle.getBundle("dev.yorushi.dma.task2.ErrorMessages", locale);
        try {
            return messages.getString(errorKey(error));
        } catch (MissingResourceException e) {
            return messages.getString("error.unknown");
        }
    }

    private static String errorKey(Throwable error) {
        // SocketTimeoutException extends IOException, not TimeoutException, so both are checked.
        if (error instanceof SocketTimeoutException || error instanceof TimeoutException) {
            return "error.timeout";
        }
        if (error instanceof UnknownHostException) {
            return "error.no_network";
        }
        if (error instanceof SSLException) {
            return "error.insecure";
        }
        if (error instanceof SecurityException) {
            return "error.permission";
        }
        return "error.unknown";
    }

    public static void demo(Report out) {
        out.section("Practice block 4. Screen state and UI architecture");
        List<String> states = new ArrayList<>();
        states.add(render(reduce(new Started<String>())));
        states.add(render(reduce(new Loaded<>(Arrays.asList("a", "b", "c")))));
        states.add(render(reduce(new Loaded<String>(Collections.emptyList()))));
        states.add(render(reduce(new Failed<String>("HTTP 500"))));
        out.item("P31", "Loading -> Success -> Empty -> Error renders " + states);
        MutableClock clock = new MutableClock(Instant.parse("2026-09-23T12:00:00Z"));
        ClickDebouncer debouncer = new ClickDebouncer(clock);
        int[] purchases = {0};
        debouncer.onClick(() -> purchases[0]++);
        clock.advance(Duration.ofMillis(120));
        debouncer.onClick(() -> purchases[0]++);
        clock.advance(Duration.ofMillis(600));
        debouncer.onClick(() -> purchases[0]++);
        out.item("P32", "clicks at 0, 120, 720 ms -> action ran " + purchases[0] + " times");
        BackStack<String> stack = new BackStack<>();
        stack.push("Home");
        stack.push("Catalog");
        stack.push("Product");
        stack.push("Cart");
        String popped = stack.pop();
        String beforeRoot = stack.toListRootFirst().toString();
        stack.popToRoot();
        out.item("P33", "pop() -> " + popped + ", stack " + beforeRoot + ", popToRoot() -> " + stack.toListRootFirst());
        out.item("P34", "PRIMARY light=" + new ThemePalette(ThemeMode.LIGHT).hex(ColorRole.PRIMARY)
                + ", dark=" + new ThemePalette(ThemeMode.DARK).hex(ColorRole.PRIMARY)
                + "; BACKGROUND dark=" + new ThemePalette(ThemeMode.DARK).hex(ColorRole.BACKGROUND));
        out.item("P35", "avatar+email+city filled -> "
                + profileCompleteness(new Profile("https://cdn/a.png", "", null, "me@mail.ru", "Tula")) + "%");
        out.item("P36", "on #FFEB3B text " + readableTextOn(0xFF, 0xEB, 0x3B) + ", on #1A237E text "
                + readableTextOn(0x1A, 0x23, 0x7E));
        out.item("P37", "950 -> " + formatCount(950) + ", 1200 -> " + formatCount(1200) + ", 1500000 -> "
                + formatCount(1_500_000) + ", 1999 -> " + formatCount(1999));
        DialogQueue dialogs = new DialogQueue();
        dialogs.show("Rate the app");
        dialogs.show("New version available");
        String visibleWhileQueued = dialogs.visible();
        dialogs.dismiss();
        out.item("P38", "2 dialogs requested, visible=\"" + visibleWhileQueued + "\", after dismiss=\""
                + dialogs.visible() + "\"");
        out.item("P39", "3 items + card + address=" + isPayButtonEnabled(3, true, true)
                + ", address unconfirmed=" + isPayButtonEnabled(3, true, false)
                + ", empty cart=" + isPayButtonEnabled(0, true, true));
        Locale russian = Locale.forLanguageTag("ru");
        out.item("P40", "UnknownHostException -> en: \"" + translateError(new UnknownHostException("api"), Locale.ENGLISH)
                + "\" / ru: \"" + translateError(new UnknownHostException("api"), russian) + "\"");
    }
}
