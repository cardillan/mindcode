package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.schemacode.schematics.Language;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstProcessor(SourcePosition sourcePosition, List<AstLink> links, AstProgram program, Language language,
                           List<AstParameter> parameters) implements AstConfiguration {

    public AstProcessor(List<AstLink> links, AstProgram program, Language language, List<AstParameter> parameters) {
        this(SourcePosition.EMPTY, links, program, language, parameters);
    }

    @Override
    public AstProcessor withEmptyPosition() {
        return new AstProcessor(SourcePosition.EMPTY,
                erasePositions(links),
                erasePosition(program),
                language,
                erasePositions(parameters));
    }
}
