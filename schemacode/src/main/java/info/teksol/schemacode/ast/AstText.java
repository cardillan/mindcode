package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

public interface AstText extends AstConfiguration {

    String getText(SchematicsBuilder builder);

    /**
     * Returns the text position within the source file
     *
     * @return the text position within the source file
     */
    SourcePosition getTextPosition(SchematicsBuilder builder);

    default int getIndent(SchematicsBuilder builder) {
        return 0;
    }
}