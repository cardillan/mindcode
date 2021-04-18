package info.teksol.mindcode.webapp;

import java.util.List;

public class NewScriptData {
    private final String name;
    private final String source;
    private final int sourceLoc;
    private final String compiled;
    private final int compiledLoc;
    private final List<String> syntaxErrors;

    NewScriptData(String name, String source, int sourceLoc, String compiled, int compiledLoc, List<String> syntaxErrors) {
        this.name = name;
        this.source = source;
        this.sourceLoc = sourceLoc;
        this.compiled = compiled;
        this.compiledLoc = compiledLoc;
        this.syntaxErrors = syntaxErrors;
    }

    public String getName() {
        return name;
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

    public boolean isLoggedIn() {
        return true;
    }
}
