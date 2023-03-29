package info.teksol.mindcode.mindustry;

import java.util.Collections;
import java.util.List;

public class CompilerOutput {
    private final String instructions;
    private final List<String> errors;
    private final List<String> messages;

    public CompilerOutput(String instructions, List<String> errors, List<String> messages) {
        this.instructions = instructions;
        this.errors = errors;
        this.messages = messages;
    }

    public CompilerOutput(String instructions, List<String> errors) {
        this.instructions = instructions;
        this.errors = errors;
        this.messages = Collections.emptyList();
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getMessages() {
        return messages;
    }
}