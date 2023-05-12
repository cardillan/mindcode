package info.teksol.schemacode.ast;

import java.util.List;

public record AstBlock(List<String> labels, String type, AstCoordinates position, AstDirection direction,
                       AstConfiguration configuration) implements AstSchemaItem {
}
