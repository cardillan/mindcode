package info.teksol.schemacode.ast;

import info.teksol.schemacode.schema.SchematicsBuilder;

public interface AstProgram extends AstSchemaItem {

    String getProgramId(SchematicsBuilder builder);

    String getProgramText(SchematicsBuilder builder);
}
