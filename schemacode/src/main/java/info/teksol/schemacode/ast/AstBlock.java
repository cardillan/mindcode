package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.List;

public record AstBlock(InputPosition inputPosition, List<String>labels, String type, AstCoordinates position, AstDirection direction,
                       AstConfiguration configuration) implements AstSchemaItem {
}
