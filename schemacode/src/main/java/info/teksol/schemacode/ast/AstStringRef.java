package info.teksol.schemacode.ast;

import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstStringRef(String reference) implements AstText {

    @Override
    public String getText(SchematicsBuilder builder) {
        return builder.getText(reference);
    }
}
