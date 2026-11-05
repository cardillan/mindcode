package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import io.github.cardillan.schemacode.schematics.SchematicsBuilder;
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
