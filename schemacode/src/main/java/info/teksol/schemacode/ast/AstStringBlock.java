package info.teksol.schemacode.ast;

import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.schema.SchematicsBuilder;

public record AstStringBlock(String literal) implements AstText {

    public AstStringBlock {
        if (literal != null && literal.isEmpty()) {
            throw new SchematicsInternalError("Empty literal.");
        }
    }

    public String getValue() {
        String unquoted = literal.substring(3, literal.length() - 3);
        // Skip first newline, if there isn't a newline (how so?), index + 1 will be equal to 0
        int index = unquoted.indexOf('\n');
        return unquoted.substring(index + 1).stripIndent();
    }

    @Override
    public String getText(SchematicsBuilder builder) {
        return getValue();
    }

    public static AstStringBlock fromText(String text) {
        return new AstStringBlock("'''\n" + text + "'''");
    }
}
