package dev.yorushi.dma.task2.themes;

import dev.yorushi.dma.task2.Report;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Theme 9 — exceptions and safe execution. */
public final class Theme9Exceptions {

    private Theme9Exceptions() {
    }

    // ---- T9.1 --------------------------------------------------------------

    /** T9.1: checked exception, so every caller has to plan for being offline. */
    public static final class NoInternetException extends Exception {
        public NoInternetException(String message) {
            super(message);
        }
    }

    public static String fetchProfile(boolean hasConnection) throws NoInternetException {
        if (!hasConnection) {
            throw new NoInternetException("no network connection");
        }
        return "{\"name\":\"yorushi\"}";
    }

    // ---- T9.2 --------------------------------------------------------------

    /** T9.2: reads {@code key=value} lines; try-with-resources closes the reader on every path. */
    public static Map<String, String> readConfig(File file) throws IOException {
        Map<String, String> config = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int separator = line.indexOf('=');
                if (separator > 0) {
                    config.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
                }
            }
        }
        return config;
    }

    // ---- T9.3 --------------------------------------------------------------

    /** T9.3: unchecked, because malformed server data is a programming/contract error. */
    public static final class InvalidUserDataException extends RuntimeException {
        public InvalidUserDataException(String message) {
            super(message);
        }

        public InvalidUserDataException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static int parseAge(String ageStr) {
        int age;
        try {
            age = Integer.parseInt(ageStr == null ? "" : ageStr.trim());
        } catch (NumberFormatException e) {
            throw new InvalidUserDataException("age is not a number: " + ageStr, e);
        }
        if (age < 0 || age > 130) {
            throw new InvalidUserDataException("age out of range 0..130: " + age);
        }
        return age;
    }

    // ---- T9.4 --------------------------------------------------------------

    /** T9.4: which catch block handled the failure of {@code action}. */
    public static String classifyFailure(Runnable action) {
        try {
            action.run();
            return "no error";
        } catch (NullPointerException e) {
            return "NullPointerException block";
        } catch (IndexOutOfBoundsException e) {
            return "IndexOutOfBoundsException block";
        } catch (Exception e) {
            return "generic Exception block (" + e.getClass().getSimpleName() + ")";
        }
    }

    // ---- T9.5 --------------------------------------------------------------

    /**
     * T9.5: reads a string from a Bundle-like map. A real {@code android.os.Bundle}
     * is typed by key the same way, so any failure — null bundle, missing key,
     * wrong type — degrades to the default instead of crashing the screen.
     */
    public static String getStringSafe(Map<String, ?> bundle, String key, String defaultValue) {
        try {
            Object value = bundle.get(key);
            return value == null ? defaultValue : (String) value;
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    public static void demo(Report out) {
        out.section("Theme 9. Exceptions");
        String offline;
        try {
            fetchProfile(false);
            offline = "no exception";
        } catch (NoInternetException e) {
            offline = "caught NoInternetException: " + e.getMessage();
        }
        out.item("T9.1", offline);
        try {
            File config = File.createTempFile("app", ".conf");
            config.deleteOnExit();
            try (PrintWriter writer = new PrintWriter(config, "UTF-8")) {
                writer.println("# local config");
                writer.println("api_url = https://api.example.com");
                writer.println("timeout_ms=15000");
            }
            out.item("T9.2", "config read with try-with-resources: " + readConfig(config));
        } catch (IOException e) {
            out.item("T9.2", "config read failed: " + e.getMessage());
        }
        String rejected;
        try {
            parseAge("140");
            rejected = "accepted";
        } catch (InvalidUserDataException e) {
            rejected = e.getMessage();
        }
        out.item("T9.3", "parseAge(\"27\")=" + parseAge("27") + ", parseAge(\"140\") -> " + rejected);
        List<String> empty = Collections.emptyList();
        String nothing = null;
        out.item("T9.4", classifyFailure(() -> nothing.length()) + " / "
                + classifyFailure(() -> empty.get(3)) + " / "
                + classifyFailure(() -> Integer.parseInt("x")));
        Map<String, Object> bundle = new LinkedHashMap<>();
        bundle.put("user_name", "yorushi");
        bundle.put("user_age", 27);
        out.item("T9.5", "user_name=" + getStringSafe(bundle, "user_name", "Guest")
                + ", user_age (int, wrong type)=" + getStringSafe(bundle, "user_age", "n/a")
                + ", null bundle=" + getStringSafe(null, "user_name", "Guest"));
    }
}
