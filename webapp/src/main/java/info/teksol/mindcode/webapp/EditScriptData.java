package info.teksol.mindcode.webapp;

import java.util.List;
import java.util.UUID;

public class EditScriptData extends NewScriptData {
    private final UUID id;
    private final List<ScriptVersion> versionHistory;

    EditScriptData(UUID id, String name, String source, int sourceLoc, String compiled, int compiledLoc, List<String> syntaxErrors, List<ScriptVersion> versionHistory) {
        super(name, source, sourceLoc, compiled, compiledLoc, syntaxErrors);
        this.id = id;
        this.versionHistory=versionHistory;
    }

    public UUID getId() {
        return id;
    }

    public List<ScriptVersion> getVersionHistory() {
        return versionHistory;
    }
}
