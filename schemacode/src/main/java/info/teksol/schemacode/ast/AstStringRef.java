package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstStringRef(SourcePosition sourcePosition, String reference) implements AstText {

    @Override
    public String getText(SchematicsBuilder builder) {
        return builder.getText(this, reference).getText(builder);
    }

    @Override
    public SourcePosition getTextPosition(SchematicsBuilder builder) {
        return builder.getText(this, reference).getTextPosition(builder);
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return builder.getText(this, reference).getIndent(builder);
    }

    @Override
    public AstStringRef withEmptyPosition() {
        return new AstStringRef(SourcePosition.EMPTY, reference);
    }
}
