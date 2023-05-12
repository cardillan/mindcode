package info.teksol.schemacode.ast;

import java.util.List;

public record AstConnections(List<AstConnection> connections) implements AstConfiguration {

    public AstConnections(AstConnection... connections) {
        this(List.of(connections));
    }
}
