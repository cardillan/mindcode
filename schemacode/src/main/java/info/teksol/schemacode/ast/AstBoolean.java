package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

public record AstBoolean(SourcePosition sourcePosition, boolean value) implements AstConfiguration {

}
