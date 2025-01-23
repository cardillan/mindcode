package info.teksol.mc.evaluator;

import static info.teksol.mc.evaluator.ExpressionEvaluator.clamp01;

public class Color {

    public static double parseColor(String symbol) {
        int r = Integer.parseInt(symbol.substring(1, 3), 16);
        int g = Integer.parseInt(symbol.substring(3, 5), 16);
        int b = Integer.parseInt(symbol.substring(5, 7), 16);
        int a = symbol.length() == 9 ? Integer.parseInt(symbol.substring(7, 9), 16) : 255;
        return toDoubleBitsClamped(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static double toDoubleBitsClamped(float r, float g, float b, float a) {
        return Double.longBitsToDouble(Color.rgba8888(r, g, b, a) & 0xffffffffL);
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

    private static String unpack(double packed) {
        int value = (int)(Double.doubleToRawLongBits(packed)),
                intR = ((value & 0xff000000) >>> 24),
                intG = ((value & 0x00ff0000) >>> 16),
                intB = ((value & 0x0000ff00) >>> 8),
                intA = ((value & 0x000000ff));

        return String.format("%%%02x%02x%02x%02x", intR, intG, intB, intA);
    }

    public static void main(String[] args) {
        double v = parseColor("%00000001");
        double w = parseColor("%ffffffff");
        System.out.println(v);
        System.out.println(w);
    }
}

