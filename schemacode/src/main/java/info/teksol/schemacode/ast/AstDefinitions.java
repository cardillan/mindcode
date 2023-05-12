package info.teksol.schemacode.ast;

import java.util.List;

public record AstDefinitions(List<AstDefinition> definitions) implements AstSchemaItem {
}
