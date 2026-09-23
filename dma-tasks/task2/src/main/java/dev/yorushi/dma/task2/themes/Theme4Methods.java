package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** Theme 4 — methods, overloading, varargs and recursion. */
public final class Theme4Methods {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@([\\w-]+\\.)+[\\w-]{2,}$");
    private static final List<String> ALLOWED_DOMAINS =
            Arrays.asList("gmail.com", "mail.ru", "yandex.ru", "hotmail.com", "outlook.com");

    private Theme4Methods() {
    }

    // ---- T4.1 --------------------------------------------------------------

    /** T4.1: syntactic e-mail check. */
    public static boolean isValid(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    /** T4.1: overload that can additionally restrict the address to known providers. */
    public static boolean isValid(String email, boolean checkDomain) {
        if (!isValid(email)) {
            return false;
        }
        if (!checkDomain) {
            return true;
        }
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase(Locale.ROOT);
        return ALLOWED_DOMAINS.contains(domain);
    }

    // ---- T4.2 --------------------------------------------------------------

    /** T4.2: cart price with thousands separated by spaces and two decimals, e.g. {@code 12 499.90 ₽}. */
    public static String formatPrice(double amount, String currencySymbol) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ROOT);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
        return format.format(amount) + " " + currencySymbol;
    }

    // ---- T4.3 --------------------------------------------------------------

    /** T4.3: total size of cached files in mebibytes. */
    public static double calculateCache(long... fileSizesInBytes) {
        long total = 0;
        for (long size : fileSizesInBytes) {
            if (size < 0) {
                throw new IllegalArgumentException("file size cannot be negative: " + size);
            }
            total += size;
        }
        return total / (1024.0 * 1024.0);
    }

    // ---- T4.4 --------------------------------------------------------------

    /** A file or a folder in the device storage tree. */
    public static final class Node {
        private final String name;
        private final List<Node> children;

        private Node(String name, List<Node> children) {
            this.name = name;
            this.children = children;
        }

        public static Node file(String name) {
            return new Node(name, Collections.emptyList());
        }

        public static Node folder(String name, Node... children) {
            return new Node(name, new ArrayList<>(Arrays.asList(children)));
        }

        public String name() {
            return name;
        }

        public List<Node> children() {
            return Collections.unmodifiableList(children);
        }
    }

    /** T4.4: counts every file and sub-folder below {@code folder}, recursively. */
    public static int countNested(Node folder) {
        int count = 0;
        for (Node child : folder.children()) {
            count += 1 + countNested(child);
        }
        return count;
    }

    // ---- T4.5 --------------------------------------------------------------

    /**
     * T4.5: numeric comparison of dotted versions, so {@code 1.12.0 > 1.9.4} and
     * {@code 1.2 == 1.2.0}; a plain string comparison would get both wrong.
     */
    public static int compareVersions(String v1, String v2) {
        String[] a = v1.split("\\.");
        String[] b = v2.split("\\.");
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int left = i < a.length ? Integer.parseInt(a[i]) : 0;
            int right = i < b.length ? Integer.parseInt(b[i]) : 0;
            if (left != right) {
                return left > right ? 1 : -1;
            }
        }
        return 0;
    }

    public static void demo(Report out) {
        out.section("Theme 4. Methods and modularity");
        out.item("T4.1", "isValid(\"user@corp.io\")=" + isValid("user@corp.io")
                + ", isValid(\"user@corp.io\", checkDomain)=" + isValid("user@corp.io", true)
                + ", isValid(\"user@mail.ru\", checkDomain)=" + isValid("user@mail.ru", true));
        out.item("T4.2", "12499.9 -> " + formatPrice(12499.9, "₽") + ", 7.5 -> " + formatPrice(7.5, "$"));
        out.item("T4.3", String.format(Locale.ROOT, "cache of 3 files = %.2f MB",
                calculateCache(1_048_576L, 2_621_440L, 524_288L)));
        Node dcim = Node.folder("DCIM",
                Node.folder("Camera", Node.file("IMG_001.jpg"), Node.file("IMG_002.jpg")),
                Node.folder("Screenshots", Node.file("shot.png"), Node.folder("Old", Node.file("a.png"))),
                Node.file(".nomedia"));
        out.item("T4.4", "elements under DCIM = " + countNested(dcim));
        out.item("T4.5", "compare(1.12.0, 1.9.4)=" + compareVersions("1.12.0", "1.9.4")
                + ", compare(2.0, 2.0.1)=" + compareVersions("2.0", "2.0.1")
                + ", compare(1.2, 1.2.0)=" + compareVersions("1.2", "1.2.0"));
    }
}
