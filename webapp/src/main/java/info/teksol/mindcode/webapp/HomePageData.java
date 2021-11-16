package info.teksol.mindcode.webapp;

import java.util.List;

public class HomePageData {
    private final String id;
    private final String sample;
    private final String source;
    private final int sourceLoc;
    private final String compiled;
    private final int compiledLoc;
    private final List<String> syntaxErrors;
    private final boolean enableOptimization;

    HomePageData(String id, String sample, String source, int sourceLoc, String compiled, int compiledLoc, List<String> syntaxErrors, boolean enableOptimization) {
        this.id = id;
        this.sample = sample;
        this.source = source;
        this.sourceLoc = sourceLoc;
        this.compiled = compiled;
        this.compiledLoc = compiledLoc;
        this.syntaxErrors = syntaxErrors;
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

    public List<String> getSyntaxErrors() {
        return syntaxErrors;
    }

    public boolean getEnableOptimization() {
        return enableOptimization;
    }

    public boolean isLoggedIn() {
        return false;
    }
}
