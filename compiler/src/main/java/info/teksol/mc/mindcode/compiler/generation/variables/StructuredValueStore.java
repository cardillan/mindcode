package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class StructuredValueStore extends CompoundValueStore {
    private final @Nullable LogicVariable object;
    private final String name;
    private final Map<String, ValueStore> members;

    public StructuredValueStore(SourcePosition sourcePosition, @Nullable LogicVariable object, String name, Map<String, ValueStore> members) {
        super(sourcePosition, ERR.REMOTE_INVALID_USE.formatted(name));
        this.object = object;
        this.name = name;
        this.members = members;
    }

    public @Nullable LogicVariable getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public @Nullable ValueStore getMember(String name) {
        return members.get(name);
    }
}
