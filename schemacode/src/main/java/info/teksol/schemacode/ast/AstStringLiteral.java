package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.antlr.v4.runtime.tree.TerminalNode;

public record AstStringLiteral(SourcePosition sourcePosition, String text) implements AstText {

    @Override
    public SourcePosition getTextPosition(SchematicsBuilder builder) {
        return sourcePosition;
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
        return new AstStringLiteral(new SourcePosition(inputFile, line, column), text);
    }

    public static AstStringLiteral fromText(String text) {
        return new AstStringLiteral(SourcePosition.EMPTY, text);
    }

    public static AstStringLiteral fromText(SourcePosition pos, String text) {
        return new AstStringLiteral(pos, text);
    }
}
