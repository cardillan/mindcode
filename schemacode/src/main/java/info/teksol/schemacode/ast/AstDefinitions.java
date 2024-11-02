package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.List;

public record AstDefinitions(InputPosition inputPosition, List<AstDefinition> definitions) implements AstSchemaItem {
}
