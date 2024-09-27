package info.teksol.schemacode.ast;

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
}
