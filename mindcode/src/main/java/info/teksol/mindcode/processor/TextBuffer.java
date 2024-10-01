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
            // We didn't replace anything -- do not report error
            return true;
        }

        int placeholderIndex = -1;
        int placeholderNumber = 10;

        for (int i = flushIndex; i < buffer.length(); i++) {
            if (buffer.charAt(i) == '{' && buffer.length() - i > 2) {
                char numChar = buffer.charAt(i + 1);

                if (numChar >= '0' && numChar <= '9' && buffer.charAt(i + 2) == '}') {
                    if (numChar - '0' < placeholderNumber) {
                        placeholderNumber = numChar - '0';
                        placeholderIndex = i;
                    }
                }
            }
        }

        if (placeholderIndex >= 0) {
            buffer.replace(placeholderIndex, placeholderIndex + 3, text);
        }

        if (buffer.length() > sizeLimit) {
            buffer.append("\nText buffer size limit exceeded.");
        }

        return placeholderIndex >= 0;
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
