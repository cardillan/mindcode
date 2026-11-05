package io.github.cardillan.mindcode.compiler.generation.variables;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.mindcode.messages.ERR;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class FormattableContent extends CompoundValueStore  {
    private final List<ValueStore> parts;

    public FormattableContent(SourcePosition sourcePosition, List<ValueStore> parts) {
        super(sourcePosition, ERR.FORMATTABLE_FORBIDDEN);
        this.parts = parts;
    }

    public List<ValueStore> getParts() {
        return parts;
    }

    @Override
    public boolean isConstantValue() {
        return true;
    }

    @Override
    public FormattableContent withSourcePosition(SourcePosition sourcePosition) {
        return new FormattableContent(sourcePosition, parts);
    }
}
