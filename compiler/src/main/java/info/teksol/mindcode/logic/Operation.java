package info.teksol.mindcode.logic;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Operation implements LogicArgument {
    EQUAL("equal", "=="),
    NOT_EQUAL("notEqual", "!="),
    LESS_THAN("lessThan", "<"),
    LESS_THAN_EQ("lessThanEq", "<="),
    GREATER_THAN("greaterThan", ">"),
    GREATER_THAN_EQ("greaterThanEq", ">="),
    STRICT_EQUAL("strictEqual", "==="),

    ADD("add", "+"),
    SUB("sub", "-"),
    MUL("mul", "*"),
    DIV("div", "/"),
    IDIV("idiv", "\\"),
    MOD("mod", "%"),
    POW("pow", "**"),

    SHL("shl", "<<"),
    SHR("shr", ">>"),
    XOR("xor", "^"),
    NOT("not", "~"),

    // Binary operations
    BINARY_AND("and", "&"),
    BINARY_OR("or",  "|"),

    // Boolean operations: guaranteed to produce 0 or 1
    BOOL_AND("land", "&&"),
    BOOL_OR("or", "||"),

    // Logical operation: produce null/zero or nonzero, can be short-circuited,
    LOGICAL_AND("land", "and"),
    LOGICAL_OR("or", "or"),

    MAX("max"),
    MIN("min"),
    ANGLE("angle"),
    ANGLEDIFF("angleDiff"),
    LEN("len"),
    NOISE(false, "noise"),
    ABS("abs"),
    LOG("log"),
    LOG10("log10"),
    FLOOR("floor"),
    CEIL("ceil"),
    SQRT("sqrt"),
    RAND(false, "rand"),

    SIN("sin"),
    COS("cos"),
    TAN("tan"),

    ASIN("asin"),
    ACOS("acos"),
    ATAN("atan"),
    ;

    private final String mlog;
    private final List<String> mindcode;
    private final boolean deterministic;
    private final boolean function;

    Operation(String mlog, String... mindcode) {
        this.mlog = mlog;
        this.mindcode = List.of(mindcode);
        this.deterministic = true;
        this.function = false;
    }
    
    Operation(String mlog) {
        this.mlog = mlog;
        this.mindcode = List.of(mlog);
        this.deterministic = true;
        this.function = true;
    }

    Operation(boolean deterministic, String mlog) {
        this.mlog = mlog;
        this.mindcode = List.of(mlog);
        this.deterministic = deterministic;
        this.function = true;
    }

    public static Operation fromMindcode(String code) {
        return MINDCODE_MAP.get(code);
    }

    public static LogicArgument fromMlog(String name) {
        return Objects.requireNonNull(MLOG_MAP.get(name), "Unknown or invalid mlog operation " + name);
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.KEYWORD;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public boolean isCondition() {
        return ordinal() <= STRICT_EQUAL.ordinal();
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public boolean isAssociative() {
        return switch(this) {
            case BINARY_AND, BINARY_OR, BOOL_AND, BOOL_OR, LOGICAL_AND, LOGICAL_OR, ADD, MUL, XOR, MIN, MAX -> true;
            default -> false;
        };
    }

    public boolean isCommutative() {
        return switch(this) {
            case EQUAL, NOT_EQUAL, STRICT_EQUAL, BINARY_AND, BINARY_OR, BOOL_AND, BOOL_OR, LOGICAL_AND, LOGICAL_OR, ADD, MUL, XOR, MIN, MAX -> true;
            default -> false;
        };
    }

    public Condition toCondition() {
        return switch (this) {
            case EQUAL -> Condition.EQUAL;
            case NOT_EQUAL -> Condition.NOT_EQUAL;
            case LESS_THAN -> Condition.LESS_THAN;
            case LESS_THAN_EQ -> Condition.LESS_THAN_EQ;
            case GREATER_THAN -> Condition.GREATER_THAN;
            case GREATER_THAN_EQ -> Condition.GREATER_THAN_EQ;
            case STRICT_EQUAL -> Condition.STRICT_EQUAL;
            default -> null;
        };
    }

    public boolean hasInverse() {
        return ordinal() < STRICT_EQUAL.ordinal();
    }

    public Operation inverse() {
        return switch (this) {
            case EQUAL -> NOT_EQUAL;
            case NOT_EQUAL -> EQUAL;
            case LESS_THAN -> GREATER_THAN_EQ;
            case GREATER_THAN_EQ -> LESS_THAN;
            case LESS_THAN_EQ -> GREATER_THAN;
            case GREATER_THAN -> LESS_THAN_EQ;
            default -> throw new MindcodeInternalError(this + " has no inverse.");
        };
    }

    public Operation swapped() {
        return switch(this) {
            case LESS_THAN -> GREATER_THAN;
            case LESS_THAN_EQ -> GREATER_THAN_EQ;
            case GREATER_THAN -> LESS_THAN;
            case GREATER_THAN_EQ -> LESS_THAN_EQ;
            default -> null;
        };
    }
    
    private static final Map<String, Operation> MINDCODE_MAP = Stream.of(values())
            .flatMap(op -> op.mindcode.stream().map(mindcode -> new Tuple2<>(op, mindcode)))
            .collect(Collectors.toMap(Tuple2::getT2, Tuple2::getT1));
    private static final Map<String, Operation> MLOG_MAP = Stream.of(values())
            .filter(v -> v.function)
            .collect(Collectors.toMap(Operation::toMlog, o -> o));
}
