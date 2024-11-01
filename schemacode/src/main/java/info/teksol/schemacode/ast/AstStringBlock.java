package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.antlr.v4.runtime.tree.TerminalNode;

public record AstStringBlock(String text, InputPosition inputPosition, int indent) implements AstText {

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

    public static AstStringBlock fromTerminalNode(InputFile inputFile, TerminalNode node) {
        String nodeText = node.getText();
        String unquoted = nodeText.substring(3, nodeText.length() - 3);
        // Skip first newline, if there isn't a newline (how so?), index + 1 will be equal to 0
        int start = unquoted.indexOf('\n');

        String textBlock = unquoted.substring(start + 1);
        String text = textBlock.stripIndent();
        int newLine = text.indexOf('\n');
        int indent = textBlock.indexOf(newLine >= 0 ? text.substring(0, newLine) : text);
        int line = node.getSymbol().getLine() + 1;
        return new AstStringBlock(text, new InputPosition(inputFile, line, 1), indent);
    }

    public static AstStringBlock fromText(String text) {
        return new AstStringBlock(text, InputPosition.EMPTY, 0);
    }
}
