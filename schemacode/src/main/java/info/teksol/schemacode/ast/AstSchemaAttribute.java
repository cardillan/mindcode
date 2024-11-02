package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstSchemaAttribute(InputPosition inputPosition, String attribute, AstSchemaItem value) implements AstSchemaItem {
}
