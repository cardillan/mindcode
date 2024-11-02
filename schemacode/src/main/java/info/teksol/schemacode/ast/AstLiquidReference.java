package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstLiquidReference(InputPosition inputPosition, String liquid) implements AstConfiguration {
}
