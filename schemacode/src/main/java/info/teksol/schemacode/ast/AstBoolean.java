package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputPosition;

public record AstBoolean(InputPosition inputPosition, boolean value) implements AstConfiguration {

}
