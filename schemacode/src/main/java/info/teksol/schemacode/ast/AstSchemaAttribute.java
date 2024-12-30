package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputPosition;

public record AstSchemaAttribute(InputPosition inputPosition, String attribute, AstSchemaItem value) implements AstSchemaItem {
}
