package dev.yorushi.dma.task4.logic;

import java.util.Random;

/** Random outcomes; a Random is passed in so tests can fix the seed. */
public final class Randoms {

    private Randoms() {
    }

    /** P02 */
    public static int rollDie(Random random) {
        return random.nextInt(6) + 1;
    }

    /** P17: no digit equals the one before it. */
    public static String pin(int length, Random random) {
        StringBuilder pin = new StringBuilder();
        int previous = -1;
        while (pin.length() < length) {
            int digit = random.nextInt(10);
            if (digit != previous) {
                pin.append(digit);
                previous = digit;
            }
        }
        return pin.toString();
    }
}
