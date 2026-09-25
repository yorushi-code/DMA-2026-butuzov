package dev.yorushi.dma.task4.logic;

import java.util.Locale;

/** Text rules of the screens. */
public final class Texts {

    private Texts() {
    }

    /** Theme 4, exercise 2. */
    public static boolean isNameLongEnough(String name) {
        return name != null && name.trim().length() >= 2;
    }

    /** P03: case and surrounding spaces do not matter. */
    public static boolean isCorrectAnswer(String answer, String expected) {
        return answer != null && answer.trim().toLowerCase(Locale.ROOT).equals(expected.toLowerCase(Locale.ROOT));
    }

    /** P05 */
    public static String reverse(String text) {
        return new StringBuilder(text).reverse().toString();
    }

    /** P07: letters only; digits, spaces and punctuation are not counted. */
    public static int countLetters(String text) {
        int letters = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isLetter(text.charAt(i))) {
                letters++;
            }
        }
        return letters;
    }

    public enum Strength { WEAK, MEDIUM, STRONG }

    /** P20: under 6 weak, 6 to 10 medium, over 10 strong. */
    public static Strength passwordStrength(String password) {
        int n = password.length();
        if (n < 6) {
            return Strength.WEAK;
        }
        return n <= 10 ? Strength.MEDIUM : Strength.STRONG;
    }
}
