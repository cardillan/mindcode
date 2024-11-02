package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.List;

public record AstConnections(InputPosition inputPosition, List<AstConnection> connections) implements AstConfiguration {

    public AstConnections(InputPosition inputPosition, AstConnection... connections) {
        this(inputPosition, List.of(connections));
    }
}
