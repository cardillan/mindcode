package info.teksol.mindcode.webapp;

import java.util.List;

public class ListScriptData {
    private final List<Script> scripts;
private final String query;

    public ListScriptData(List<Script> scripts, String query) {
        this.scripts = scripts;
        this.query = query;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public String getQuery() {
        return query;
    }
}
