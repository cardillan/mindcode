package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstStringConstant(SourcePosition sourcePosition, String name, AstText value) implements AstDefinition {

    public AstStringConstant(String name, AstText value) {
        this(SourcePosition.EMPTY, name, value);
    }

    @Override
    public AstStringConstant withEmptyPosition() {
        return new AstStringConstant(SourcePosition.EMPTY, name, erasePosition(value));
    }
}
