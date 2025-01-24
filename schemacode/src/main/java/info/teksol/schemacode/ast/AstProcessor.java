package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import info.teksol.schemacode.schematics.Language;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public record AstProcessor(SourcePosition sourcePosition, List<AstLink> links, @Nullable AstProgram program, Language language) implements AstConfiguration {

    @Override
    public AstProcessor withEmptyPosition() {
        return new AstProcessor(SourcePosition.EMPTY,
                erasePositions(links),
                eraseNullablePosition(program),
                language);
    }
}
