package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

import java.util.List;

public record AstSchematic(InputPosition inputPosition, List<AstSchemaAttribute> attributes, List<AstBlock> blocks) implements AstDefinition {
}
