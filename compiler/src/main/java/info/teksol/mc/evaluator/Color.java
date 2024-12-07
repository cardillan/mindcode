package info.teksol.mc.evaluator;

import static info.teksol.mc.evaluator.ExpressionEvaluator.clamp01;

public class Color {

    public static double parseColor(String symbol) {
        int r = Integer.parseInt(symbol.substring(1, 3), 16);
        int g = Integer.parseInt(symbol.substring(3, 5), 16);
        int b = Integer.parseInt(symbol.substring(5, 7), 16);
        int a = symbol.length() == 9 ? Integer.parseInt(symbol.substring(7, 9), 16) : 255;

        return toDoubleBits(r, g, b, a);
    }

    public static String toColorLiteral(double r, double g, double b, double a) {
        int intR = (int) (255 * clamp01(r));
        int intG = (int) (255 * clamp01(g));
        int intB = (int) (255 * clamp01(b));
        int intA = (int) (255 * clamp01(a));

        return intA == 255
                ? String.format("%%%02x%02x%02x", intR, intG, intB)
                : String.format("%%%02x%02x%02x%02x", intR, intG, intB, intA);
    }

    public static double toDoubleBits(int r, int g, int b, int a) {
        return toDoubleBits(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static double toDoubleBits(float r, float g, float b, float a) {
        return Double.longBitsToDouble(Color.rgba8888(r, g, b, a) & 0xffffffffL);
    }

    public static int rgba8888(float r, float g, float b, float a) {
        return ((int) (r * 255) << 24) | ((int) (g * 255) << 16) | ((int) (b * 255) << 8) | (int) (a * 255);
    }
}

