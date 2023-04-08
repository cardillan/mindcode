package info.teksol.mindcode.webapp;

import java.util.List;

public class HomePageData {
    private final String id;
    private final String sample;
    private final String source;
    private final int sourceLoc;
    private final String compiled;
    private final int compiledLoc;
    private final List<String> errors;
    private final List<String> warnings;
    private final List<String> messages;
    private final boolean enableOptimization;

    HomePageData(String id, String sample, String source, int sourceLoc, String compiled, int compiledLoc,
            List<String> errors, List<String> warnings, List<String> messages, boolean enableOptimization) {
        this.id = id;
        this.sample = sample;
        this.source = source;
        this.sourceLoc = sourceLoc;
        this.compiled = compiled;
        this.compiledLoc = compiledLoc;
        this.errors = errors;
        this.warnings = warnings;
        this.messages = messages;
        this.enableOptimization = enableOptimization;
    }

    public String getId() {
        return id;
    }

    public String getSample() {
        return sample;
    }

    public String getSource() {
        return source;
    }

    public int getSourceLoc() {
        return sourceLoc;
    }

    public String getCompiled() {
        return compiled;
    }

    public int getCompiledLoc() {
        return compiledLoc;
    }

    public boolean getEnableOptimization() {
        return enableOptimization;
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getMessages() {
        return messages;
    }

    public boolean getHasErrors() {
        return !errors.isEmpty();
    }

    public boolean getHasMessages() {
        return !warnings.isEmpty() || !messages.isEmpty();
    }

    public boolean isLoggedIn() {
        return false;
    }
}
