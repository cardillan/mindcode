package info.teksol.mc.evaluator;

import org.jspecify.annotations.NullMarked;

import static info.teksol.mc.evaluator.ExpressionEvaluator.clamp01;

@NullMarked
public class Color {

    public static double parseColor(String symbol) {
        return parseColor(symbol, symbol.startsWith("%") ? 1 : 0);
    }

    private static double parseColor(String symbol, int offset) {
        int r = Integer.parseInt(symbol, offset, offset + 2, 16);
        int g = Integer.parseInt(symbol, offset + 2, offset + 4, 16);
        int b = Integer.parseInt(symbol, offset + 4, offset + 6, 16);
        int a = symbol.length() - offset == 8 ? Integer.parseInt(symbol, offset + 6, offset + 8, 16) : 255;
        return toDoubleBitsClamped(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static double toDoubleBitsClamped(float r, float g, float b, float a) {
        return Double.longBitsToDouble(rgba8888(r, g, b, a) & 0xffffffffL);
    }

    private static int rgba8888(float r, float g, float b, float a) {
        return ((int) (r * 255) << 24) | ((int) (g * 255) << 16) | ((int) (b * 255) << 8) | (int) (a * 255);
    }

    public static String toColorLiteral(double r, double g, double b, double a) {
        return toColorLiteralClamped(
                clamp01(r),
                clamp01(g),
                clamp01(b),
                clamp01(a)
        );
    }

    public static String toColorLiteralClamped(float r, float g, float b, float a) {
        int intR = (int) (255 * r);
        int intG = (int) (255 * g);
        int intB = (int) (255 * b);
        int intA = (int) (255 * a);

        return intA == 255
                ? String.format("%%%02x%02x%02x", intR, intG, intB)
                : String.format("%%%02x%02x%02x%02x", intR, intG, intB, intA);
    }

    public static String unpack(double packed) {
        int value = (int)(Double.doubleToRawLongBits(packed)),
                intR = ((value & 0xff000000) >>> 24),
                intG = ((value & 0x00ff0000) >>> 16),
                intB = ((value & 0x0000ff00) >>> 8),
                intA = ((value & 0x000000ff));

        return String.format("%%%02x%02x%02x%02x", intR, intG, intB, intA);
    }
}

