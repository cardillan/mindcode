package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputPosition;

public record AstDirection(InputPosition inputPosition, String direction) implements AstSchemaItem {
}
