package dev.yorushi.dma.task2;

import dev.yorushi.dma.task2.practice.Block1Auth;
import dev.yorushi.dma.task2.practice.Block2Lists;
import dev.yorushi.dma.task2.practice.Block3Network;
import dev.yorushi.dma.task2.practice.Block4UiState;
import dev.yorushi.dma.task2.practice.Block5Hardware;
import dev.yorushi.dma.task2.themes.Theme1Primitives;
import dev.yorushi.dma.task2.themes.Theme2ControlFlow;
import dev.yorushi.dma.task2.themes.Theme3ArraysStrings;
import dev.yorushi.dma.task2.themes.Theme4Methods;
import dev.yorushi.dma.task2.themes.Theme5Encapsulation;
import dev.yorushi.dma.task2.themes.Theme6Inheritance;
import dev.yorushi.dma.task2.themes.Theme7Interfaces;
import dev.yorushi.dma.task2.themes.Theme8Collections;
import dev.yorushi.dma.task2.themes.Theme9Exceptions;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Runs the demonstration of every task-2 item: 45 theme exercises and 50
 * practical items. Run it with {@code ./gradlew :task2:run}; the process exits
 * with status 1 if any item is missing from the output.
 */
public final class Task2Runner {

    public static final int EXPECTED_ITEMS = 95;

    private static final List<Consumer<Report>> DEMOS = Collections.unmodifiableList(Arrays.asList(
            Theme1Primitives::demo,
            Theme2ControlFlow::demo,
            Theme3ArraysStrings::demo,
            Theme4Methods::demo,
            Theme5Encapsulation::demo,
            Theme6Inheritance::demo,
            Theme7Interfaces::demo,
            Theme8Collections::demo,
            Theme9Exceptions::demo,
            Block1Auth::demo,
            Block2Lists::demo,
            Block3Network::demo,
            Block4UiState::demo,
            Block5Hardware::demo));

    private Task2Runner() {
    }

    /** @return number of items demonstrated */
    public static int runAll(Report out) {
        for (Consumer<Report> demo : DEMOS) {
            demo.accept(out);
        }
        return out.itemCount();
    }

    public static void main(String[] args) {
        System.out.println("DMA-2026 / Task 2 - Java for Android: 45 theme exercises + 50 practical items");
        int items = runAll(new Report(System.out::println));
        System.out.println();
        System.out.println("Items demonstrated: " + items + " / " + EXPECTED_ITEMS);
        if (items != EXPECTED_ITEMS) {
            System.exit(1);
        }
    }
}
