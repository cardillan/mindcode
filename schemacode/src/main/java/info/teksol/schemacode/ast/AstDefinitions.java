package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

import java.util.List;

public record AstDefinitions(SourcePosition sourcePosition, List<AstDefinition> definitions) implements AstSchemaItem {
}
