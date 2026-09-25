package dev.yorushi.dma.task4.logic;

/** Project 1: which scores are milestones and what colour each one gives the screen. */
public final class Clicker {

    private Clicker() {
    }

    public static final String START_COLOR = "#F5F5F5";

    /** The colour to switch to at this score, or null when the score is not a milestone. */
    public static String milestoneColor(int score) {
        if (score == 10) {
            return "#E8F5E9";
        }
        if (score == 50) {
            return "#FFF9C4";
        }
        return null;
    }
}
