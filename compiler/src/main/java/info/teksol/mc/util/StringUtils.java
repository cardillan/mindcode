package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

import java.util.Collection;
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

    public static String joinCompressedRanges(Collection<Integer> numbers) {
        if (numbers.isEmpty()) return "";

        // Sort to ensure ranges are detected correctly
        List<Integer> list = numbers.stream().sorted().toList();

        StringBuilder result = new StringBuilder();
        int start = list.getFirst();
        int prev = start;

        for (int i = 1; i < list.size(); i++) {
            int curr = list.get(i);

            if (curr == prev + 1) {
                // Continue the range
                prev = curr;
            } else {
                // End the previous range
                appendRange(result, start, prev);
                start = prev = curr;
            }
        }

        // Append the final range
        appendRange(result, start, prev);

        return result.toString();
    }

    private static void appendRange(StringBuilder sb, int start, int end) {
        if (!sb.isEmpty()) sb.append(", ");
        if (start == end) {
            sb.append(start);
        } else if (start + 1 == end) {
            sb.append(start).append(", ").append(end);
        } else {
            sb.append(start).append(" .. ").append(end);
        }
    }
}
