package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstStringBlock(InputPosition inputPosition, String text, int indent) implements AstText {

    @Override
    public InputPosition getTextPosition(SchematicsBuilder builder) {
        return inputPosition;
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return indent;
    }

    @Override
    public String getText(SchematicsBuilder builder) {
        return text;
    }

    public static AstStringBlock fromTerminalNode(InputPosition inputPosition, String nodeText) {
        String unquoted = nodeText.substring(3, nodeText.length() - 3);
        // Skip first newline, if there isn't a newline (how so?), index + 1 will be equal to 0
        int start = unquoted.indexOf('\n');

        String textBlock = unquoted.substring(start + 1);
        String text = textBlock.stripIndent();
        int newLine = text.indexOf('\n');
        int indent = textBlock.indexOf(newLine >= 0 ? text.substring(0, newLine) : text);
        return new AstStringBlock(inputPosition.nextLine(), text, indent);
    }

    public static AstStringBlock fromText(String text) {
        return new AstStringBlock(InputPosition.EMPTY, text, 0);
    }
}
