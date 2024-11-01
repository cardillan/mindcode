package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public interface AstText extends AstConfiguration {

    String getText(SchematicsBuilder builder);

    /**
     * Returns the text position within the source file
     *
     * @return the text position within the source file
     */
    InputPosition getTextPosition(SchematicsBuilder builder);

    default int getIndent(SchematicsBuilder builder) {
        return 0;
    }
}