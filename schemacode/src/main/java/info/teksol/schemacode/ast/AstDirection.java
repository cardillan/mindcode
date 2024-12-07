package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

public record AstDirection(SourcePosition sourcePosition, String direction) implements AstSchemaItem {
}
