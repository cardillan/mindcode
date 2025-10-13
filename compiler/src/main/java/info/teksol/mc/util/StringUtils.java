package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

import java.util.List;

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

    public static String joinUsingOr(List<String> parts) {
        return switch (parts.size()) {
            case 0 -> "";
            case 1 -> parts.getFirst();
            case 2 -> String.join(" or ", parts);
            default -> String.join(", ", parts.subList(0, parts.size() - 1))
                       + " or " + parts.getLast();
        };
    }

    public static String joinUsingAnd(List<String> parts) {
        return switch (parts.size()) {
            case 0 -> "";
            case 1 -> parts.getFirst();
            case 2 -> String.join(" and ", parts);
            default -> String.join(", ", parts.subList(0, parts.size() - 1))
                       + " and " + parts.getLast();
        };
    }
}
