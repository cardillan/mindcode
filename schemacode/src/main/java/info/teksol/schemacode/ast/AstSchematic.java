package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

import java.util.List;

public record AstSchematic(SourcePosition sourcePosition, List<AstSchemaAttribute> attributes, List<AstBlock> blocks) implements AstDefinition {
}
