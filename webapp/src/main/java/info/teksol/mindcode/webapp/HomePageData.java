package info.teksol.mindcode.webapp;

import info.teksol.mc.Version;

import java.util.List;

public class HomePageData {
    private final String version = Version.getVersion();
    private final String id;
    private final String sample;
    private final String source;
    private final int sourceLoc;
    private final String compiled;
    private final int compiledLoc;
    private final List<WebappMessage> errors;
    private final List<WebappMessage> warnings;
    private final List<WebappMessage> messages;
    private final String optimizationLevel;
    private final String runOutput;
    private final int runSteps;

    HomePageData(String id, String sample, String source, int sourceLoc, String compiled, int compiledLoc,
                 List<WebappMessage> errors, List<WebappMessage> warnings, List<WebappMessage> messages,
                 String optimizationLevel, String runOutput, int runSteps) {
        this.id = id;
        this.sample = sample;
        this.source = source;
        this.sourceLoc = sourceLoc;
        this.compiled = compiled;
        this.compiledLoc = compiledLoc;
        this.errors = errors;
        this.warnings = warnings;
        this.messages = messages;
        this.optimizationLevel = optimizationLevel;
        this.runOutput = runOutput;
        this.runSteps = runSteps;
    }

    public String getVersion() {
        return version;
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

    public List<WebappMessage> getErrors() {
        return errors;
    }

    public List<WebappMessage> getWarnings() {
        return warnings;
    }

    public List<WebappMessage> getMessages() {
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

    public String getOptimizationLevel() {
        return optimizationLevel;
    }

    public String getRunOutput() {
        return runOutput == null ? "" : runOutput;
    }

    public int getRunSteps() {
        return runSteps;
    }
}
