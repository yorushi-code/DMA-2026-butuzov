package dev.yorushi.dma.task5.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 38: photos numbered in the order they were picked. Unpicking one renumbers the
 * photos picked after it, as in messenger galleries.
 */
public final class SelectionOrder {

    private final List<Integer> picked = new ArrayList<>();

    /** Picks the item, or unpicks it if it was picked. */
    public void toggle(int item) {
        int at = picked.indexOf(item);
        if (at >= 0) {
            picked.remove(at);
        } else {
            picked.add(item);
        }
    }

    /** 1-based place in the selection, or 0 if the item is not picked. */
    public int position(int item) {
        return picked.indexOf(item) + 1;
    }

    public int size() {
        return picked.size();
    }
}
