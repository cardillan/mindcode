package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstUnitReference(InputPosition inputPosition, String unit) implements AstConfiguration {
}
