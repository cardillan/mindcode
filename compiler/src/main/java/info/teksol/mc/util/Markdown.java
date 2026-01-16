package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class Markdown {

    public static String toMarkdownTable(List<List<String>> rows) {
        if (!rows.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            toMarkdownTable(rows, sb);
            return sb.toString();
        } else {
            return "";
        }
    }

    public static void toMarkdownTable(List<List<String>> rows, StringBuilder sb) {
        if (rows.isEmpty()) return;

        int columnCount = rows.stream().mapToInt(List::size).max().orElse(0);
        int[] colWidths = new int[columnCount];

        // Compute max width for each column
        for (List<String> row : rows) {
            for (int i = 0; i < columnCount; i++) {
                String cell = i < row.size() ? row.get(i) : "";
                colWidths[i] = Math.max(colWidths[i], cell.length());
            }
        }

        for (int r = 0; r < rows.size(); r++) {
            List<String> row = rows.get(r);
            for (int i = 0; i < columnCount; i++) {
                String cell = i < row.size() ? row.get(i) : "";
                sb.append("| ").append(cell).append(" ".repeat( colWidths[i] - cell.length() + 1));
            }
            sb.append("|\n");

            // After the first row, add the Markdown separator line
            if (r == 0) {
                for (int i = 0; i < columnCount; i++) {
                    sb.append('|').append("-".repeat(colWidths[i] + 2));
                }
                sb.append("|\n");
            }
        }
    }
}
