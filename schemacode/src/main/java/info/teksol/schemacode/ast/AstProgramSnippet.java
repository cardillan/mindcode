package info.teksol.schemacode.ast;

import info.teksol.schemacode.schematics.SchematicsBuilder;

public interface AstProgramSnippet extends AstSchemaItem {

    String getProgramId(SchematicsBuilder builder);

    String getProgramText(SchematicsBuilder builder);
}
