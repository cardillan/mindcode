package info.teksol.schemacode.ast;

import info.teksol.mindcode.InputPosition;

public record AstBoolean(InputPosition inputPosition, boolean value) implements AstConfiguration {

}
