package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

public record AstSchemaAttribute(SourcePosition sourcePosition, String attribute, AstSchemaItem value) implements AstSchemaItem {
}
