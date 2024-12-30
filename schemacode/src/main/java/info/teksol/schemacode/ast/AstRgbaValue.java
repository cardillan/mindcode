package info.teksol.schemacode.ast;

import info.teksol.mc.common.InputPosition;

public record AstRgbaValue(InputPosition inputPosition, int red, int green, int blue, int alpha) implements AstColor {

}
