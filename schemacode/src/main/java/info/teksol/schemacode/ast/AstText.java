package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.common.TextOffset;
import info.teksol.schemacode.schematics.SchematicsBuilder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AstText extends AstConfiguration {

    String getText(SchematicsBuilder builder);

    /// Returns the text position within the source file
    ///
    /// @return the text position within the source file
    SourcePosition getTextPosition(SchematicsBuilder builder);

    default int getIndent(SchematicsBuilder builder) {
        return 0;
    }

    default TextOffset getTextOffset(SchematicsBuilder builder) {
        SourcePosition textPosition = getTextPosition(builder);
        return TextOffset.of(textPosition.line() - 1, textPosition.column() - 1, getIndent(builder));
    }
}
