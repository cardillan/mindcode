package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstDirection(InputPosition inputPosition, String direction) implements AstSchemaItem {
}
