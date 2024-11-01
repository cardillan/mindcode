package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public interface AstProgramSnippet extends AstSchemaItem {

    String getProgramId(SchematicsBuilder builder);

    String getProgramText(SchematicsBuilder builder);

    InputPosition getInputPosition(SchematicsBuilder builder);

    default int getIndent(SchematicsBuilder builder) {
        return 0;
    }
}
