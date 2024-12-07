package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstStringBlock(SourcePosition sourcePosition, String text, int indent) implements AstText {

    @Override
    public SourcePosition getTextPosition(SchematicsBuilder builder) {
        return sourcePosition;
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return indent;
    }

    @Override
    public String getText(SchematicsBuilder builder) {
        return text;
    }

    public static AstStringBlock fromTerminalNode(SourcePosition sourcePosition, String nodeText) {
        String unquoted = nodeText.substring(3, nodeText.length() - 3);
        // Skip first newline, if there isn't a newline (how so?), index + 1 will be equal to 0
        int start = unquoted.indexOf('\n');

        String textBlock = unquoted.substring(start + 1);
        String text = textBlock.stripIndent();
        int newLine = text.indexOf('\n');
        int indent = textBlock.indexOf(newLine >= 0 ? text.substring(0, newLine) : text);
        return new AstStringBlock(sourcePosition.nextLine(), text, indent);
    }

    public static AstStringBlock fromText(String text) {
        return new AstStringBlock(SourcePosition.EMPTY, text, 0);
    }
}
