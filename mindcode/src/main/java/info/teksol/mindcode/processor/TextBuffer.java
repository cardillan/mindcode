package info.teksol.mindcode.processor;

import java.util.ArrayList;
import java.util.List;

public class TextBuffer {
    private final int sizeLimit;
    private final List<String> output = new ArrayList<>();

    private final StringBuilder buffer = new StringBuilder();
    private int flushIndex = 0;

    public TextBuffer(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public void print(String text) {
        if (buffer.length() <= sizeLimit) {
            output.add(text);
            buffer.append(text);
            if (buffer.length() > sizeLimit) {
                buffer.append("\nText buffer size limit exceeded.");
            }
        }
    }

    /**
     * Replaces a formatter placeholder in the output buffer with the value.
     * @param text value to use for formatting
     * @return false if the formatting placeholder was not found
     */
    public boolean format(String text) {
        if (buffer.length() > sizeLimit) {
            // We didn't even try to replace anything -- do not report error
            return true;
        }

        int index = findLeastPlaceholderIndex();

        if (index >= 0) {
            buffer.replace(index, index + 3, text);
            if (buffer.length() > sizeLimit) {
                buffer.append("\nText buffer size limit exceeded.");
            }
        }

        return index >= 0;
    }

    /**
     * Finds the first occurrence of a placeholder with the least value in the text buffer and
     * returns it's position (index in the text buffer).
     * @return position of the placeholder to be replaced
     */
    private int findLeastPlaceholderIndex() {
        int index = -1;
        char minValue = '9' + 1;                // Least found value so far, won't be matched again
        int limit = buffer.length() - 2;

        for (int i = flushIndex; i < limit; i++) {
            if (buffer.charAt(i) == '{') {
                char placeholder = buffer.charAt(i + 1);
                if (placeholder >= '0' && placeholder < minValue && buffer.charAt(i + 2) == '}') {
                    minValue = placeholder;
                    index = i;
                }
            }
        }

        return index;
    }

    public void printflush() {
        flushIndex = buffer.length();
    }

    /**
     * Provides the individual values of print instructions. Format instruction doesn't
     * affect this output.
     *
     * @return the list of individual print statement values
     */
    public List<String> getPrintOutput() {
        return output;
    }

    /**
     * @return the joined and possible formatted output of print and format instructions.
     */
    public String getTextBuffer() {
        return buffer.toString();
    }
}
