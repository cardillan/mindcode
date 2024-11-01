package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstStringRef(String reference) implements AstText {

    @Override
    public String getText(SchematicsBuilder builder) {
        return builder.getText(reference).getText(builder);
    }

    @Override
    public InputPosition getTextPosition(SchematicsBuilder builder) {
        return builder.getText(reference).getTextPosition(builder);
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return builder.getText(reference).getIndent(builder);
    }
}
