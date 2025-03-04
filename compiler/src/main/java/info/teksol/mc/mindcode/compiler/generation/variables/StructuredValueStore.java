package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class StructuredValueStore extends CompoundValueStore {
    private final String name;
    private final Map<String, ValueStore> members;

    public StructuredValueStore(SourcePosition sourcePosition, String name, Map<String, ValueStore> members) {
        super(sourcePosition, ERR.REMOTE_INVALID_USE.formatted(name));
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public @Nullable ValueStore getMember(String name) {
        return members.get(name);
    }
}
