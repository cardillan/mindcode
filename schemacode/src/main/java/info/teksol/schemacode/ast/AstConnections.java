package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

import java.util.List;

public record AstConnections(SourcePosition sourcePosition, List<AstConnection> connections) implements AstConfiguration {

    public AstConnections(SourcePosition sourcePosition, AstConnection... connections) {
        this(sourcePosition, List.of(connections));
    }
}
