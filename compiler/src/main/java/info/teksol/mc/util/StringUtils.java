package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class StringUtils {

    public static String firstLowerCase(String s) {
        return s.isEmpty() ? "" : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String firstUpperCase(String s) {
        return s.isEmpty() ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String titleCase(String s) {
        return s.isEmpty() ? "" : Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static String normalizeLineEndings(String string) {
        return string.replaceAll("\\R", System.lineSeparator());
    }
}
