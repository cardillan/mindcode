package info.teksol.mindcode.processor;

import java.util.ArrayList;
import java.util.List;

public class OutputBuffer {
    private final int sizeLimit;
    private final List<String> output = new ArrayList<>();

    private int size = 0;
    private boolean overflow = false;

    public OutputBuffer(int sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    public void append(String text) {
        if (!overflow) {
            output.add(text);
            size += text.length();
            if (size > sizeLimit) {
                overflow = true;
                output.add("\nText buffer size limit exceeded.");
            }
        }
    }

    public List<String> getOutput() {
        return output;
    }

    public String getJoinedOutput() {
        return String.join("", output);
    }
}
