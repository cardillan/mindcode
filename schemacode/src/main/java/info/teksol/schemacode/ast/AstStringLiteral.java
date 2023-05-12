package info.teksol.schemacode.ast;

import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.schema.SchematicsBuilder;

public record AstStringLiteral(String literal) implements AstText {

    public AstStringLiteral {
        if (literal != null && literal.isEmpty()) {
            throw new SchematicsInternalError("Empty literal.");
        }
    }

    public String getValue() {
        // Strip quotes
        return literal.substring(1, literal.length() - 1);
    }

    @Override
    public String getText(SchematicsBuilder builder) {
        return getValue();
    }

    public static AstStringLiteral fromText(String text) {
        return new AstStringLiteral('"' + text + '"');
    }
}
