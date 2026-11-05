package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstStringRef(SourcePosition sourcePosition, String reference) implements AstText {

    public AstStringRef(String reference) {
        this(SourcePosition.EMPTY, reference);
    }

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
