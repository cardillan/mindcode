package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AstProgramSnippetText(SourcePosition sourcePosition, AstText programText) implements AstProgramSnippet {

    public AstProgramSnippetText(AstText programText) {
        this(SourcePosition.EMPTY, programText);
    }

    @Override
    public String getProgramId(SchematicsBuilder builder) {
        return "embedded code";
    }

    @Override
    public String getProgramText(SchematicsBuilder builder) {
        return programText.getText(builder);
    }

    @Override
    public SourcePosition getSourcePosition(SchematicsBuilder builder) {
        return programText.getTextPosition(builder);
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return programText.getIndent(builder);
    }

    @Override
    public AstProgramSnippetText withEmptyPosition() {
        return new AstProgramSnippetText(SourcePosition.EMPTY, erasePosition(programText));
    }
}
