package info.teksol.schemacode.ast;

import java.util.List;

public record AstSchematics(List<AstSchemaAttribute> attributes, List<AstBlock> blocks) implements AstDefinition {
}
