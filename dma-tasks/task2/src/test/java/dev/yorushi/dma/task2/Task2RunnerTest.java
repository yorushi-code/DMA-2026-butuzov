package dev.yorushi.dma.task2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Task2RunnerTest {

    @Test
    @DisplayName("Runner demonstrates all 95 items, each id exactly once")
    void everyItemDemonstratedOnce() {
        List<String> lines = new ArrayList<>();
        int count = Task2Runner.runAll(new Report(lines::add));
        assertEquals(Task2Runner.EXPECTED_ITEMS, count);

        Pattern id = Pattern.compile("^\\[(T\\d\\.\\d|P\\d{2}) *]");
        Set<String> ids = new HashSet<>();
        for (String line : lines) {
            Matcher m = id.matcher(line);
            if (m.find()) {
                assertTrue(ids.add(m.group(1)), "duplicate item " + m.group(1));
            }
        }
        for (int theme = 1; theme <= 9; theme++) {
            for (int item = 1; item <= 5; item++) {
                assertTrue(ids.contains("T" + theme + "." + item), "missing T" + theme + "." + item);
            }
        }
        for (int item = 1; item <= 50; item++) {
            assertTrue(ids.contains(String.format("P%02d", item)), "missing P" + item);
        }
    }
}
