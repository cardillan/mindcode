package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

public record AstStringConstant(SourcePosition sourcePosition, String name, AstText value) implements AstDefinition {
}
