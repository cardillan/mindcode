package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.antlr.v4.runtime.tree.TerminalNode;

public record AstStringLiteral(InputPosition inputPosition, String text) implements AstText {

    @Override
    public InputPosition getTextPosition(SchematicsBuilder builder) {
        return inputPosition;
    }

    @Override
    public String getText(SchematicsBuilder builder) {
        return text;
    }

    public static AstStringLiteral fromTerminalNode(InputFile inputFile, TerminalNode node) {
        String nodeText = node.getText();
        String text = nodeText.substring(1, nodeText.length() - 1);
        int line = node.getSymbol().getLine();
        int column = node.getSymbol().getCharPositionInLine() + 2;
        return new AstStringLiteral(new InputPosition(inputFile, line, column), text);
    }

    public static AstStringLiteral fromText(String text) {
        return new AstStringLiteral(InputPosition.EMPTY, text);
    }

    public static AstStringLiteral fromText(String text, int line, int column) {
        return new AstStringLiteral(new InputPosition(InputFile.EMPTY, line, column), text);
    }
}
