package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputPosition;

import java.util.List;

public record AstDefinitions(InputPosition inputPosition, List<AstDefinition> definitions) implements AstSchemaItem {
}
