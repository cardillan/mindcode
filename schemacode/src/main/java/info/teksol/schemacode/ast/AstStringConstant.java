package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstStringConstant(InputPosition inputPosition, String name, AstText value) implements AstDefinition {
}
