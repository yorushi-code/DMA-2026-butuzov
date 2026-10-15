package dev.yorushi.dma.task5.logic;

/** Item 13: the checklist under the new-password field. */
public final class PasswordRules {

    public static final int MIN_LENGTH = 8;

    private PasswordRules() {
    }

    public static boolean longEnough(CharSequence password) {
        return password.length() >= MIN_LENGTH;
    }

    public static boolean hasDigit(CharSequence password) {
        return password.chars().anyMatch(Character::isDigit);
    }

    public static boolean mixedCase(CharSequence password) {
        return password.chars().anyMatch(Character::isLowerCase)
                && password.chars().anyMatch(Character::isUpperCase);
    }

    /** The repeat matches, and there is something to match. */
    public static boolean matches(CharSequence password, CharSequence repeat) {
        return password.length() > 0 && password.toString().contentEquals(repeat);
    }

    public static boolean allMet(CharSequence password, CharSequence repeat) {
        return longEnough(password) && hasDigit(password) && mixedCase(password) && matches(password, repeat);
    }
}
