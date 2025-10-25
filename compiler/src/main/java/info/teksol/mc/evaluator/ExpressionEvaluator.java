package info.teksol.mc.evaluator;

import info.teksol.mc.mindcode.logic.arguments.Operation;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@NullMarked
public class ExpressionEvaluator {
    public static final double doubleDegRad = 0.017453292519943295;
    public static final double doubleRadDeg = 57.29577951308232;

    public static @Nullable LogicOperation getOperation(Operation operation) {
        return OPERATIONS.get(operation);
    }

    public static LogicOperation existingOperation(Operation operation) {
        return Objects.requireNonNull(OPERATIONS.get(operation));
    }

    public static int getNumberOfArguments(Operation operation) {
        return ARGUMENTS.getOrDefault(operation, -1);
    }

    public static void evaluatePackColor(LogicWritable target, LogicReadable r, LogicReadable g, LogicReadable b, LogicReadable a) {
        target.setDoubleValue(Color.toDoubleBitsClamped(
                clamp01(r.getDoubleValue()),
                clamp01(g.getDoubleValue()),
                clamp01(b.getDoubleValue()),
                clamp01(a.getDoubleValue()))
        );
    }

    public static void evaluateUnpackColor(LogicReadable color, LogicWritable r, LogicWritable g, LogicWritable b, LogicWritable a) {
        Color unpacked = Color.fromDouble(color.getDoubleValue());
        r.setDoubleValue(unpacked.r());
        g.setDoubleValue(unpacked.g());
        b.setDoubleValue(unpacked.b());
        a.setDoubleValue(unpacked.a());
    }

    public static boolean invalid(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    public static float clamp01(double value) {
        return Math.max(Math.min((float)value, 1f), 0f);
    }

    public static boolean equals(LogicReadable a, LogicReadable b) {
        if (a.isObject() && b.isObject()) {
            return Objects.equals(a.getObject(), b.getObject());
        } else {
            return equals(a.getDoubleValue(), b.getDoubleValue());
        }
    }

    public static boolean strictlyEquals(LogicReadable a, LogicReadable b) {
        return (a.isObject() == b.isObject()) && (a.isObject()
                ? Objects.equals(a.getObject(), b.getObject())
                : a.getDoubleValue() == b.getDoubleValue()
        );
    }

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }

    public static boolean isTrue(double a) {
        return Math.abs(a) >= 0.000001;
    }

    public static boolean isFalse(double a) {
        return Math.abs(a) <= 0.000001;
    }

    private static final Map<Operation, LogicOperation> OPERATIONS = createOperationsMap();
    private static final Map<Operation, Integer> ARGUMENTS = createArgumentsMap();

    private static Map<Operation, LogicOperation> createOperationsMap() {
        Map<Operation, LogicOperation> map = new EnumMap<>(Operation.class);

        map.put(Operation.ADD,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() + b.getDoubleValue()));
        map.put(Operation.SUB,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() - b.getDoubleValue()));
        map.put(Operation.MUL,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() * b.getDoubleValue()));
        map.put(Operation.DIV,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() / b.getDoubleValue()));
        map.put(Operation.IDIV,             (r, a, b) -> r.setDoubleValue(Math.floor(a.getDoubleValue() / b.getDoubleValue())));
        map.put(Operation.MOD,              (r, a, b) -> r.setDoubleValue(a.getDoubleValue() % b.getDoubleValue()));
        map.put(Operation.EMOD,             (r, a, b) -> r.setDoubleValue(((a.getDoubleValue() % b.getDoubleValue()) + b.getDoubleValue()) % b.getDoubleValue()));
        map.put(Operation.POW,              (r, a, b) -> r.setDoubleValue(Math.pow(a.getDoubleValue(), b.getDoubleValue())));

        map.put(Operation.EQUAL,            (r, a, b) -> r.setBooleanValue(equals(a, b)));
        map.put(Operation.NOT_EQUAL,        (r, a, b) -> r.setBooleanValue(!equals(a, b)));
        map.put(Operation.LESS_THAN,        (r, a, b) -> r.setBooleanValue(a.getDoubleValue() < b.getDoubleValue()));
        map.put(Operation.LESS_THAN_EQ,     (r, a, b) -> r.setBooleanValue(a.getDoubleValue() <= b.getDoubleValue()));
        map.put(Operation.GREATER_THAN,     (r, a, b) -> r.setBooleanValue(a.getDoubleValue() > b.getDoubleValue()));
        map.put(Operation.GREATER_THAN_EQ,  (r, a, b) -> r.setBooleanValue(a.getDoubleValue() >= b.getDoubleValue()));
        map.put(Operation.STRICT_EQUAL,     (r, a, b) -> r.setBooleanValue(strictlyEquals(a, b)));
        map.put(Operation.STRICT_NOT_EQUAL, (r, a, b) -> r.setBooleanValue(!strictlyEquals(a, b)));

        map.put(Operation.SHL,              (r, a, b) -> r.setLongValue(a.getLongValue() <<  b.getLongValue()));
        map.put(Operation.SHR,              (r, a, b) -> r.setLongValue(a.getLongValue() >>  b.getLongValue()));
        map.put(Operation.USHR,             (r, a, b) -> r.setLongValue(a.getLongValue() >>> b.getLongValue()));
        map.put(Operation.BITWISE_XOR,      (r, a, b) -> r.setLongValue(a.getLongValue() ^ b.getLongValue()));

        map.put(Operation.BITWISE_NOT,      (r, a, b) -> r.setLongValue(~a.getLongValue()));
        map.put(Operation.BOOLEAN_NOT,      (r, a, b) -> r.setBooleanValue(a.getLongValue() == 0));
        map.put(Operation.LOGICAL_NOT,      (r, a, b) -> r.setBooleanValue(a.getLongValue() == 0));

        map.put(Operation.BITWISE_AND,      (r, a, b) -> r.setLongValue(a.getLongValue() & b.getLongValue()));
        map.put(Operation.BOOLEAN_AND,      (r, a, b) -> r.setBooleanValue(a.getDoubleValue() != 0 && b.getDoubleValue() != 0));
        map.put(Operation.LOGICAL_AND,      (r, a, b) -> r.setBooleanValue(a.getDoubleValue() != 0 && b.getDoubleValue() != 0));

        map.put(Operation.BITWISE_OR,       (r, a, b) -> r.setLongValue(a.getLongValue() | b.getLongValue()));
        map.put(Operation.BOOLEAN_OR,       (r, a, b) -> r.setLongValue(a.getLongValue() | b.getLongValue()));
        map.put(Operation.LOGICAL_OR,       (r, a, b) -> r.setLongValue(a.getLongValue() | b.getLongValue()));

        map.put(Operation.MAX,              (r, a, b) -> r.setDoubleValue(Math.max(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.MIN,              (r, a, b) -> r.setDoubleValue(Math.min(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.ANGLE,            (r, a, b) -> r.setDoubleValue(angle(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.ANGLEDIFF,        (r, a, b) -> r.setDoubleValue(angleDiff(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.LEN,              (r, a, b) -> r.setDoubleValue(len(a.getDoubleValue(), b.getDoubleValue())));
        map.put(Operation.ABS,              (r, a, b) -> r.setDoubleValue(Math.abs(a.getDoubleValue())));
        map.put(Operation.SIGN,             (r, a, b) -> r.setDoubleValue(Math.signum(a.getDoubleValue())));
        map.put(Operation.LOG,              (r, a, b) -> r.setDoubleValue(Math.log(a.getDoubleValue())));
        map.put(Operation.LOGN,             (r, a, b) -> r.setDoubleValue(Math.log(a.getDoubleValue() / Math.log(b.getDoubleValue()))));
        map.put(Operation.LOG10,            (r, a, b) -> r.setDoubleValue(Math.log10(a.getDoubleValue())));
        map.put(Operation.FLOOR,            (r, a, b) -> r.setDoubleValue(Math.floor(a.getDoubleValue())));
        map.put(Operation.CEIL,             (r, a, b) -> r.setDoubleValue(Math.ceil(a.getDoubleValue())));
        map.put(Operation.ROUND,            (r, a, b) -> r.setDoubleValue(Math.round(a.getDoubleValue())));
        map.put(Operation.SQRT,             (r, a, b) -> r.setDoubleValue(Math.sqrt(a.getDoubleValue())));
        map.put(Operation.RAND,             (r, a, b) -> r.setDoubleValue(rnd.nextDouble() * a.getDoubleValue()));

        map.put(Operation.SIN,              (r, a, b) -> r.setDoubleValue(Math.sin(a.getDoubleValue() * doubleDegRad)));
        map.put(Operation.COS,              (r, a, b) -> r.setDoubleValue(Math.cos(a.getDoubleValue() * doubleDegRad)));
        map.put(Operation.TAN,              (r, a, b) -> r.setDoubleValue(Math.tan(a.getDoubleValue() * doubleDegRad)));

        map.put(Operation.ASIN,             (r, a, b) -> r.setDoubleValue(Math.asin(a.getDoubleValue()) * doubleRadDeg));
        map.put(Operation.ACOS,             (r, a, b) -> r.setDoubleValue(Math.acos(a.getDoubleValue()) * doubleRadDeg));
        map.put(Operation.ATAN,             (r, a, b) -> r.setDoubleValue(Math.atan(a.getDoubleValue()) * doubleRadDeg));

        return map;
    }

    private static Map<Operation, Integer> createArgumentsMap() {
        Map<Operation, Integer> map = new EnumMap<>(Operation.class);

        map.put(Operation.MAX,          2);
        map.put(Operation.MIN,          2);
        map.put(Operation.ANGLE,        2);
        map.put(Operation.ANGLEDIFF,    2);
        map.put(Operation.LEN,          2);
        map.put(Operation.ABS,          1);
        map.put(Operation.SIGN,         1);
        map.put(Operation.LOG,          1);
        map.put(Operation.LOG10,        1);
        map.put(Operation.FLOOR,        1);
        map.put(Operation.CEIL,         1);
        map.put(Operation.ROUND,        1);
        map.put(Operation.SQRT,         1);
        map.put(Operation.SIN,          1);
        map.put(Operation.COS,          1);
        map.put(Operation.TAN,          1);

        map.put(Operation.ASIN,         1);
        map.put(Operation.ACOS,         1);
        map.put(Operation.ATAN,         1);

        return map;
    }

    private static double angle(double x, double y) {
        double result = Math.atan(y / x) * 180 / Math.PI;
        return (result < 0) ? result + 360 : result;
    }

    private static double angleDiff(double a, double b){
        a = mod(a, 360.0);
        b = mod(b, 360.0);
        return Math.min((a - b) < 0 ? a - b + 360 : a - b, (b - a) < 0 ? b - a + 360 : b - a);
    }

    private static double mod(double f, double n){
        return ((f % n) + n) % n;
    }

    private static double len(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    private static final Random rnd = new Random();

    private ExpressionEvaluator() { }
}
