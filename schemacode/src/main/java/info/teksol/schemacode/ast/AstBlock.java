package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

import java.util.List;

public record AstBlock(SourcePosition sourcePosition, List<String>labels, String type, AstCoordinates position, AstDirection direction,
                       AstConfiguration configuration) implements AstSchemaItem {
}
