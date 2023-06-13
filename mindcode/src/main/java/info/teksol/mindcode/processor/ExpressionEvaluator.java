package info.teksol.mindcode.processor;

import info.teksol.mindcode.logic.Operation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ExpressionEvaluator {

    public static OperationEval getOperation(Operation operation) {
        return OPERATIONS.get(operation);
    }

    public static int getNumberOfArguments(Operation operation) {
        return ARGUMENTS.getOrDefault(operation, -1);
    }

    public static void evaluatePackColor(MindustryResult target, MindustryValue r, MindustryValue g, MindustryValue b, MindustryValue a) {
        double result = toDoubleBits(clamp(r.getDoubleValue()), clamp(g.getDoubleValue()), clamp(b.getDoubleValue()),
                clamp(a.getDoubleValue()));

        target.setDoubleValue(result);
    }


    private static float clamp(double value){
        return Math.max(Math.min((float)value, 1f), 0f);
    }

    public static double toDoubleBits(float r, float g, float b, float a){
        return Double.longBitsToDouble(rgba8888(r, g, b, a) & 0x00000000_ffffffffL);
    }

    private static int rgba8888(float r, float g, float b, float a){
        return ((int)(r * 255) << 24) | ((int)(g * 255) << 16) | ((int)(b * 255) << 8) | (int)(a * 255);
    }



    public static boolean equals(MindustryValue a, MindustryValue b) {
        if (a.isObject() && b.isObject()) {
            return Objects.equals(a.getObject(), b.getObject());
        } else {
            return equals(a.getDoubleValue(), b.getDoubleValue());
        }
    }

    public static boolean strictEquals(MindustryValue a, MindustryValue b) {
        return (a.getMindustryValueType().isNumeric() == b.getMindustryValueType().isNumeric()) && equals(a, b);
    }

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }

    private static final Map<Operation, OperationEval> OPERATIONS = createOperationsMap();
    private static final Map<Operation, Integer> ARGUMENTS = createArgumentsMap();

    private static Map<Operation, OperationEval> createOperationsMap() {
        Map<Operation, OperationEval> map = new EnumMap<>(Operation.class);

        map.put(Operation.ADD,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() + b.getDoubleValue()));
        map.put(Operation.SUB,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() - b.getDoubleValue()));
        map.put(Operation.MUL,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() * b.getDoubleValue()));
        map.put(Operation.DIV,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() / b.getDoubleValue()));
        map.put(Operation.IDIV,             (r, a, b) -> r.setDoubleValue(Math.floor(a.getDoubleValue() / b.getDoubleValue())));
        map.put(Operation.MOD,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() % b.getDoubleValue()));
        map.put(Operation.POW,              (r, a, b) -> r.setDoubleValue(Math.pow(a.getDoubleValue(), b.getDoubleValue())));

        map.put(Operation.EQUAL,            (r, a, b) -> r.setBooleanValue(equals(a, b)));
        map.put(Operation.NOT_EQUAL,        (r, a, b) -> r.setBooleanValue(!equals(a, b)));
        map.put(Operation.LAND,             (r, a, b) -> r.setBooleanValue(a.getDoubleValue() != 0 && b.getDoubleValue() != 0));
        map.put(Operation.LESS_THAN,        (r, a, b) -> r.setBooleanValue(a.getDoubleValue() < b.getDoubleValue()));
        map.put(Operation.LESS_THAN_EQ,     (r, a, b) -> r.setBooleanValue(a.getDoubleValue() <= b.getDoubleValue()));
        map.put(Operation.GREATER_THAN,     (r, a, b) -> r.setBooleanValue(a.getDoubleValue() > b.getDoubleValue()));
        map.put(Operation.GREATER_THAN_EQ,  (r, a, b) -> r.setBooleanValue(a.getDoubleValue() >= b.getDoubleValue()));
        map.put(Operation.STRICT_EQUAL,     (r, a, b) -> r.setBooleanValue(strictEquals(a, b)));

        map.put(Operation.SHL,              (r, a, b) -> r.setLongValue(a.getLongValue() <<  b.getLongValue()));
        map.put(Operation.SHR,              (r, a, b) -> r.setLongValue(a.getLongValue() >>  b.getLongValue()));
        map.put(Operation.OR,               (r, a, b) -> r.setLongValue(a.getLongValue() |  b.getLongValue()));
        map.put(Operation.AND,              (r, a, b) -> r.setLongValue(a.getLongValue() &  b.getLongValue()));
        map.put(Operation.XOR,              (r, a, b) -> r.setLongValue(a.getLongValue() ^  b.getLongValue()));
        map.put(Operation.NOT,              (r, a, b) -> r.setLongValue(~a.getLongValue()));

        map.put(Operation.MAX,              (r, a, b) -> r.setDoubleValue(Math.max(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.MIN,              (r, a, b) -> r.setDoubleValue(Math.min(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.ANGLE,            (r, a, b) -> r.setDoubleValue(angle(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.LEN,              (r, a, b) -> r.setDoubleValue(len(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.ABS,              (r, a, b) -> r.setDoubleValue(Math.abs(a.getDoubleValue())));
        map.put(Operation.LOG,              (r, a, b) -> r.setDoubleValue(Math.log(a.getDoubleValue())));
        map.put(Operation.LOG10,            (r, a, b) -> r.setDoubleValue(Math.log10(a.getDoubleValue())));
        map.put(Operation.FLOOR,            (r, a, b) -> r.setDoubleValue(Math.floor(a.getDoubleValue())));
        map.put(Operation.CEIL,             (r, a, b) -> r.setDoubleValue(Math.ceil(a.getDoubleValue())));
        map.put(Operation.SQRT,             (r, a, b) -> r.setDoubleValue(Math.sqrt(a.getDoubleValue())));
        map.put(Operation.RAND,             (r, a, b) -> r.setDoubleValue(rnd.nextDouble() * a.getDoubleValue()));

        map.put(Operation.SIN,              (r, a, b) -> r.setDoubleValue(Math.sin(a.getDoubleValue())));
        map.put(Operation.COS,              (r, a, b) -> r.setDoubleValue(Math.cos(a.getDoubleValue())));
        map.put(Operation.TAN,              (r, a, b) -> r.setDoubleValue(Math.tan(a.getDoubleValue())));

        map.put(Operation.ASIN,             (r, a, b) -> r.setDoubleValue(Math.asin(a.getDoubleValue())));
        map.put(Operation.ACOS,             (r, a, b) -> r.setDoubleValue(Math.acos(a.getDoubleValue())));
        map.put(Operation.ATAN,             (r, a, b) -> r.setDoubleValue(Math.atan(a.getDoubleValue())));

        return map;
    }

    private static Map<Operation, Integer> createArgumentsMap() {
        Map<Operation, Integer> map = new EnumMap<>(Operation.class);

        map.put(Operation.MAX,          2);
        map.put(Operation.MIN,          2);
        map.put(Operation.ANGLE,        2);
        map.put(Operation.LEN,          2);
        map.put(Operation.ABS,          1);
        map.put(Operation.LOG,          1);
        map.put(Operation.LOG10,        1);
        map.put(Operation.FLOOR,        1);
        map.put(Operation.CEIL,         1);
        map.put(Operation.SQRT,         1);
        map.put(Operation.SIN,          1);
        map.put(Operation.COS,          1);
        map.put(Operation.TAN,          1);

        // TODO: these functions are only available in Mindustry Logic 7
        // Shouldn't be evaluated when compiling for V6.
        map.put(Operation.ASIN,         1);
        map.put(Operation.ACOS,         1);
        map.put(Operation.ATAN,         1);

        return map;
    }

    private static double angle(double x, double y) {
        double result = Math.atan(y / x) * 180 / Math.PI;
        return (result < 0) ? result + 360 : result;
    }

    private static double len(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    private static final Random rnd = new Random();

    private ExpressionEvaluator() { }
}
