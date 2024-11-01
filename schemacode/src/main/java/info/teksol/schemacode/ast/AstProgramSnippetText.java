package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public record AstProgramSnippetText(AstText programText) implements AstProgramSnippet {

    @Override
    public String getProgramId(SchematicsBuilder builder) {
        return "embedded code";
    }

    @Override
    public String getProgramText(SchematicsBuilder builder) {
        return programText.getText(builder);
    }

    @Override
    public InputPosition getInputPosition(SchematicsBuilder builder) {
        return programText.getTextPosition(builder);
    }

    @Override
    public int getIndent(SchematicsBuilder builder) {
        return programText.getIndent(builder);
    }
}
