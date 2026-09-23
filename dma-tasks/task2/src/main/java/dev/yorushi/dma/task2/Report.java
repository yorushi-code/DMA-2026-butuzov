package dev.yorushi.dma.task2;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Line-oriented output, decoupled from {@code System.out} so that any front end —
 * the console runner or a future Android screen — renders the same demonstration.
 */
public final class Report {

    private final Consumer<String> sink;
    private int itemCount;

    public Report(Consumer<String> sink) {
        this.sink = Objects.requireNonNull(sink, "sink");
    }

    public void section(String title) {
        sink.accept("");
        sink.accept("== " + title + " ==");
    }

    /**
     * @param id   item identifier: {@code T3.2} for a theme exercise, {@code P17} for a practical task
     * @param text what the item computed
     */
    public void item(String id, String text) {
        itemCount++;
        sink.accept(String.format(Locale.ROOT, "[%-4s] %s", id, text));
    }

    public int itemCount() {
        return itemCount;
    }
}
