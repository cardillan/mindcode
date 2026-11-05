package io.github.cardillan.schemacode.ast;

import io.github.cardillan.mindcode.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstConnections(SourcePosition sourcePosition, List<AstConnection> connections) implements AstConfiguration {

    public AstConnections(AstConnection... connections) {
        this(SourcePosition.EMPTY, List.of(connections));
    }

    @Override
    public AstConnections withEmptyPosition() {
        return new AstConnections(SourcePosition.EMPTY, erasePositions(connections));
    }
}
