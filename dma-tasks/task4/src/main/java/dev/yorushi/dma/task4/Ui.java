package dev.yorushi.dma.task4;

import android.widget.EditText;
import java.util.Locale;

/**
 * Reading numbers from EditText without the beginner's crash (mistake no. 2 of
 * the assignment: Integer.parseInt("")). Each method shows the problem on the
 * field itself and returns null, so callers only check for null.
 */
public final class Ui {

    private Ui() {
    }

    public static String text(EditText field) {
        return field.getText().toString().trim();
    }

    /**
     * True if the field is not empty; otherwise marks it. A passing field loses
     * any earlier error: setText() does not clear it, only typing does.
     */
    public static boolean required(EditText field) {
        if (text(field).isEmpty()) {
            field.setError(field.getContext().getString(R.string.error_required));
            return false;
        }
        field.setError(null);
        return true;
    }

    /** Any number; a comma is accepted as the decimal separator. */
    public static Double number(EditText field) {
        if (!required(field)) {
            return null;
        }
        try {
            return Double.parseDouble(text(field).replace(',', '.'));
        } catch (NumberFormatException e) {
            field.setError(field.getContext().getString(R.string.error_number));
            return null;
        }
    }

    public static Double positive(EditText field) {
        return atLeast(number(field), field, false);
    }

    public static Double nonNegative(EditText field) {
        return atLeast(number(field), field, true);
    }

    public static Long whole(EditText field) {
        if (!required(field)) {
            return null;
        }
        try {
            return Long.parseLong(text(field));
        } catch (NumberFormatException e) {
            field.setError(field.getContext().getString(R.string.error_number));
            return null;
        }
    }

    public static Integer positiveInt(EditText field) {
        Long value = whole(field);
        return toInt(atLeast(value == null ? null : value.doubleValue(), field, false));
    }

    public static Integer nonNegativeInt(EditText field) {
        Long value = whole(field);
        return toInt(atLeast(value == null ? null : value.doubleValue(), field, true));
    }

    private static Integer toInt(Double value) {
        return value == null || value > Integer.MAX_VALUE ? null : value.intValue();
    }

    private static Double atLeast(Double value, EditText field, boolean zeroAllowed) {
        if (value == null) {
            return null;
        }
        if (value < 0 || (!zeroAllowed && value == 0)) {
            field.setError(field.getContext().getString(zeroAllowed ? R.string.error_negative : R.string.error_positive));
            return null;
        }
        return value;
    }

    /** Two decimals, in the device's locale. */
    public static String money(double value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    /** Up to two decimals, without trailing zeros. */
    public static String decimal(double value) {
        String s = String.format(Locale.getDefault(), "%.2f", value);
        return s.replaceAll("[.,]?0+$", "");
    }
}
