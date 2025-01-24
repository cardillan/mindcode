package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public record AstConnections(SourcePosition sourcePosition, List<AstConnection> connections) implements AstConfiguration {

    public AstConnections(SourcePosition sourcePosition, AstConnection... connections) {
        this(sourcePosition, List.of(connections));
    }

    @Override
    public AstConnections withEmptyPosition() {
        return new AstConnections(SourcePosition.EMPTY, erasePositions(connections));
    }
}
