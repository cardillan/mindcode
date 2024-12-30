package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputPosition;

import java.util.List;

public record AstSchematic(InputPosition inputPosition, List<AstSchemaAttribute> attributes, List<AstBlock> blocks) implements AstDefinition {
}
