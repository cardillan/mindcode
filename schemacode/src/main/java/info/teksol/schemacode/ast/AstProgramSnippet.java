package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstProgramSnippet extends AstSchemaItem {

    String getProgramId(SchematicsBuilder builder);

    String getProgramText(SchematicsBuilder builder);

    SourcePosition getSourcePosition(SchematicsBuilder builder);

    default int getIndent(SchematicsBuilder builder) {
        return 0;
    }
}
