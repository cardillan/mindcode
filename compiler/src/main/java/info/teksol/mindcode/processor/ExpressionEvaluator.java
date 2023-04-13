package info.teksol.mindcode.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ExpressionEvaluator {

    public static String translateOperator(String operator) {
        return TRANSLATIONS.get(operator);
    }

    public static Operation getOperation(String operation) {
        return OPERATIONS.get(operation);
    }

    public static boolean isDeterministic(String operation) {
        switch (operation) {
            case "noise":
            case "rand":
                return false;

            default:
                return true;
        }
    }

    public static int getNumberOfArguments(String functionName) {
        return ARGUMENTS.getOrDefault(functionName, -1);
    }

    public static boolean equals(Variable a, Variable b) {
        if (a.isObject() && b.isObject()) {
            return Objects.equals(a.getObject(), b.getObject());
        } else {
            return equals(a.getDoubleValue(), b.getDoubleValue());
        }
    }
    public static boolean strictEquals(Variable a, Variable b) {
        return (a.getType().isNumeric() == b.getType().isNumeric()) && equals(a, b);
    }

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < 0.000001;
    }

    private static final Map<String, String> TRANSLATIONS = createTranslationMap();
    private static final Map<String, Operation> OPERATIONS = createOperationsMap();
    private static final Map<String, Integer> ARGUMENTS = createArgumentsMap();

    private static Map<String, String> createTranslationMap() {
        Map<String, String> map = new HashMap<>();
        map.put("+",    "add");
        map.put("-",    "sub");
        map.put("*",    "mul");
        map.put("/",    "div");
        map.put("\\",   "idiv");
        map.put("==",   "equal");
        map.put("!=",   "notEqual");
        map.put("<",    "lessThan");
        map.put("<=",   "lessThanEq");
        map.put(">=",   "greaterThanEq");
        map.put(">",    "greaterThan");
        map.put("===",  "strictEqual");
        map.put("**",   "pow");
        map.put("||",   "or");
        map.put("or",   "or");
        map.put("&&",   "land");  // logical-and
        map.put("and",  "land");  // logical-and
        map.put("%",    "mod");
        map.put("<<",   "shl");
        map.put(">>",   "shr");
        map.put("&",    "and");
        map.put("|",    "or");
        map.put("^",    "xor");
        map.put("~",    "not");
        return Map.copyOf(map);
    }

    private static Map<String, Operation> createOperationsMap() {
        Map<String, Operation> map = new HashMap<>();

        map.put("add",          (r, a, b) -> r.setDoubleValue(a.getDoubleValue() + b.getDoubleValue()));
        map.put("sub",          (r, a, b) -> r.setDoubleValue(a.getDoubleValue() - b.getDoubleValue()));
        map.put("mul",          (r, a, b) -> r.setDoubleValue(a.getDoubleValue() * b.getDoubleValue()));
        map.put("div",          (r, a, b) -> r.setDoubleValue(a.getDoubleValue() / b.getDoubleValue()));
        map.put("idiv",         (r, a, b) -> r.setDoubleValue(Math.floor(a.getDoubleValue() / b.getDoubleValue())));
        map.put("mod",          (r, a, b) -> r.setDoubleValue(a.getDoubleValue() % b.getDoubleValue()));
        map.put("pow",          (r, a, b) -> r.setDoubleValue(Math.pow(a.getDoubleValue(), b.getDoubleValue())));

        map.put("equal",        (r, a, b) -> r.setBooleanValue(equals(a, b)));
        map.put("notEqual",     (r, a, b) -> r.setBooleanValue(!equals(a, b)));
        map.put("land",         (r, a, b) -> r.setBooleanValue(a.getDoubleValue() != 0 && b.getDoubleValue() != 0));
        map.put("lessThan",     (r, a, b) -> r.setBooleanValue(a.getDoubleValue() < b.getDoubleValue()));
        map.put("lessThanEq",   (r, a, b) -> r.setBooleanValue(a.getDoubleValue() <= b.getDoubleValue()));
        map.put("greaterThan",  (r, a, b) -> r.setBooleanValue(a.getDoubleValue() > b.getDoubleValue()));
        map.put("greaterThanEq",(r, a, b) -> r.setBooleanValue(a.getDoubleValue() >= b.getDoubleValue()));
        map.put("strictEqual",  (r, a, b) -> r.setBooleanValue(strictEquals(a, b)));

        map.put("shl",          (r, a, b) -> r.setLongValue(a.getLongValue() <<  b.getLongValue()));
        map.put("shr",          (r, a, b) -> r.setLongValue(a.getLongValue() >>  b.getLongValue()));
        map.put("or",           (r, a, b) -> r.setLongValue(a.getLongValue() |  b.getLongValue()));
        map.put("and",          (r, a, b) -> r.setLongValue(a.getLongValue() &  b.getLongValue()));
        map.put("xor",          (r, a, b) -> r.setLongValue(a.getLongValue() ^  b.getLongValue()));
        map.put("not",          (r, a, b) -> r.setLongValue(~a.getLongValue()));

        map.put("max",          (r, a, b) -> r.setDoubleValue(Math.max(a.getDoubleValue(), b.getDoubleValue())));
        map.put("min",          (r, a, b) -> r.setDoubleValue(Math.min(a.getDoubleValue(), b.getDoubleValue())));
        map.put("angle",        (r, a, b) -> r.setDoubleValue(angle(a.getDoubleValue(), b.getDoubleValue())));
        map.put("len",          (r, a, b) -> r.setDoubleValue(len(a.getDoubleValue(), b.getDoubleValue())));
//        map.put("noise",        (r, a, b) -> {});     // Not supported here
        map.put("abs",          (r, a, b) -> r.setDoubleValue(Math.abs(a.getDoubleValue())));
        map.put("log",          (r, a, b) -> r.setDoubleValue(Math.log(a.getDoubleValue())));
        map.put("log10",        (r, a, b) -> r.setDoubleValue(Math.log10(a.getDoubleValue())));
        map.put("floor",        (r, a, b) -> r.setDoubleValue(Math.floor(a.getDoubleValue())));
        map.put("ceil",         (r, a, b) -> r.setDoubleValue(Math.ceil(a.getDoubleValue())));
        map.put("sqrt",         (r, a, b) -> r.setDoubleValue(Math.sqrt(a.getDoubleValue())));
        map.put("rand",         (r, a, b) -> r.setDoubleValue(rnd.nextDouble() * a.getDoubleValue()));

        map.put("sin",          (r, a, b) -> r.setDoubleValue(Math.sin(a.getDoubleValue())));
        map.put("cos",          (r, a, b) -> r.setDoubleValue(Math.cos(a.getDoubleValue())));
        map.put("tan",          (r, a, b) -> r.setDoubleValue(Math.tan(a.getDoubleValue())));

        map.put("asin",         (r, a, b) -> r.setDoubleValue(Math.asin(a.getDoubleValue())));
        map.put("acos",         (r, a, b) -> r.setDoubleValue(Math.acos(a.getDoubleValue())));
        map.put("atan",         (r, a, b) -> r.setDoubleValue(Math.atan(a.getDoubleValue())));

        return map;
    }

    private static Map<String, Integer> createArgumentsMap() {
        Map<String, Integer> map = new HashMap<>();

        map.put("max",          2);
        map.put("min",          2);
        map.put("angle",        2);
        map.put("len",          2);
        map.put("abs",          1);
        map.put("log",          1);
        map.put("log10",        1);
        map.put("floor",        1);
        map.put("ceil",         1);
        map.put("sqrt",         1);
        map.put("sin",          1);
        map.put("cos",          1);
        map.put("tan",          1);

        // TODO: these functions are only available in Mindustry Logic 7
        // Shouldn't be evaluated when compiling for V6.
        map.put("asin",         1);
        map.put("acos",         1);
        map.put("atan",         1);

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
