package info.teksol.schemacode.ast;

import info.teksol.mc.common.SourcePosition;

public record AstRgbaValue(SourcePosition sourcePosition, int red, int green, int blue, int alpha) implements AstColor {

}
