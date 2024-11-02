package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstUnitCommandReference(InputPosition inputPosition, String item) implements AstConfiguration {
}
