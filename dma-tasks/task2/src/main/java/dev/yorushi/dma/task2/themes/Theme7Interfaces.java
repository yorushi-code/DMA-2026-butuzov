package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Theme 7 — abstract classes and interfaces. */
public final class Theme7Interfaces {

    private Theme7Interfaces() {
    }

    // ---- T7.1 --------------------------------------------------------------

    /** T7.1: storage contract independent of the backing medium. */
    public interface KeyValueStorage {
        void save(String key, String value);

        String get(String key);

        void clear();
    }

    /** T7.1: in-memory implementation, e.g. for tests or a session cache. */
    public static final class MemoryStorage implements KeyValueStorage {
        private final Map<String, String> values = new HashMap<>();

        @Override
        public void save(String key, String value) {
            values.put(key, value);
        }

        @Override
        public String get(String key) {
            return values.get(key);
        }

        @Override
        public void clear() {
            values.clear();
        }
    }

    // ---- T7.2 --------------------------------------------------------------

    /** T7.2: outcome of an asynchronous image load. */
    public interface ImageLoadCallback {
        void onSuccess(String bitmapRef);

        void onError(Throwable error);
    }

    /** T7.2: simulated loader that reports back through the callback. */
    public static void loadImage(String url, ImageLoadCallback callback) {
        if (url == null || !url.startsWith("https://")) {
            callback.onError(new IllegalArgumentException("only https images are allowed: " + url));
            return;
        }
        callback.onSuccess("bitmap@" + Integer.toHexString(url.hashCode()));
    }

    // ---- T7.3 --------------------------------------------------------------

    /** T7.3: background task listener; progress reporting is optional for implementors. */
    public interface BackgroundTaskListener {
        default void onProgress(int percentage) {
            // Listeners that only care about completion may ignore progress.
        }

        void onComplete();
    }

    /** T7.3: runs a fake task in {@code steps} increments and reports to the listener. */
    public static void runTask(int steps, BackgroundTaskListener listener) {
        for (int step = 1; step <= steps; step++) {
            listener.onProgress(step * 100 / steps);
        }
        listener.onComplete();
    }

    // ---- T7.4 --------------------------------------------------------------

    public interface Playable {
        String play();

        String stop();
    }

    public interface Shareable {
        String shareViaBluetooth();
    }

    /** T7.4: a class fulfilling two independent contracts. */
    public static final class MediaFile implements Playable, Shareable {
        private final String name;
        private boolean playing;

        public MediaFile(String name) {
            this.name = name;
        }

        @Override
        public String play() {
            playing = true;
            return "playing " + name;
        }

        @Override
        public String stop() {
            playing = false;
            return "stopped " + name;
        }

        @Override
        public String shareViaBluetooth() {
            return "sending " + name + " over Bluetooth OPP";
        }

        public boolean isPlaying() {
            return playing;
        }
    }

    // ---- T7.5 --------------------------------------------------------------

    /** T7.5: single-method validation contract usable as a lambda. */
    @FunctionalInterface
    public interface PredicateValidator<T> {
        boolean validate(T data);

        default PredicateValidator<T> and(PredicateValidator<T> other) {
            return data -> validate(data) && other.validate(data);
        }
    }

    public static void demo(Report out) {
        out.section("Theme 7. Abstract classes and interfaces");
        KeyValueStorage storage = new MemoryStorage();
        storage.save("token", "eyJhbGciOi");
        String before = storage.get("token");
        storage.clear();
        out.item("T7.1", "saved token=" + before + ", after clear=" + storage.get("token"));
        List<String> results = new ArrayList<>();
        ImageLoadCallback collect = new ImageLoadCallback() {
            @Override
            public void onSuccess(String bitmapRef) {
                results.add("ok " + bitmapRef);
            }

            @Override
            public void onError(Throwable error) {
                results.add("error " + error.getMessage());
            }
        };
        loadImage("https://cdn.example/avatar.png", collect);
        loadImage("http://insecure.example/a.png", collect);
        out.item("T7.2", String.join(" / ", results));
        List<Integer> progress = new ArrayList<>();
        runTask(4, new BackgroundTaskListener() {
            @Override
            public void onProgress(int percentage) {
                progress.add(percentage);
            }

            @Override
            public void onComplete() {
                progress.add(-1);
            }
        });
        out.item("T7.3", "progress events " + progress + " (-1 = complete)");
        MediaFile song = new MediaFile("track.mp3");
        out.item("T7.4", song.play() + "; " + song.shareViaBluetooth() + "; " + song.stop());
        PredicateValidator<String> notBlank = s -> s != null && !s.trim().isEmpty();
        PredicateValidator<String> shortEnough = s -> s.length() <= 12;
        PredicateValidator<String> nickname = notBlank.and(shortEnough);
        out.item("T7.5", "nickname \"yorushi\"=" + nickname.validate("yorushi")
                + ", \"   \"=" + nickname.validate("   ")
                + ", \"a_very_long_nickname\"=" + nickname.validate("a_very_long_nickname"));
    }
}
