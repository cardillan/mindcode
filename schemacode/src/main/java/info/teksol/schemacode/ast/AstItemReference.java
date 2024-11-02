package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstItemReference(InputPosition inputPosition, String item) implements AstConfiguration {
}
