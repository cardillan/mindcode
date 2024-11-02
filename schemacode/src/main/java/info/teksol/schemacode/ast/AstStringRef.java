package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstStringRef(InputPosition inputPosition, String reference) implements AstText {

    @Override
    public String getText(SchematicsBuilder builder) {
        return builder.getText(this, reference).getText(builder);
    }

    @Override
    public InputPosition getTextPosition(SchematicsBuilder builder) {
        return builder.getText(this, reference).getTextPosition(builder);
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return builder.getText(this, reference).getIndent(builder);
    }
}
