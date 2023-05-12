package info.teksol.schemacode.ast;

public record AstStringConstant(String name, AstText value) implements AstDefinition {
}
