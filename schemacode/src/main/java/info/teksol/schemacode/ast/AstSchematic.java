package info.teksol.schemacode.ast;

import java.util.List;

public record AstSchematic(List<AstSchemaAttribute> attributes, List<AstBlock> blocks) implements AstDefinition {
}
