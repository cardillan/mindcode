package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstBlockReference(InputPosition inputPosition, String item) implements AstConfiguration {
}
