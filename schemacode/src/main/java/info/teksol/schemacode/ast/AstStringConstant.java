package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstStringConstant(SourcePosition sourcePosition, String name, AstText value) implements AstDefinition {

    @Override
    public AstStringConstant withEmptyPosition() {
        return new AstStringConstant(SourcePosition.EMPTY, name, erasePosition(value));
    }
}
