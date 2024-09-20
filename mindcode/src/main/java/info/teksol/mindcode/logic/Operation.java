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

    LAND("land", "and", "&&"),

    SHL("shl", "<<"),
    SHR("shr", ">>"),
    OR("or",  "or", "|", "||"),
    AND("and", "&"),
    XOR("xor", "^"),
    NOT("not", "~"),

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

    Operation(String mlog, String... mindcode) {
        this.mlog = mlog;
        this.mindcode = List.of(mindcode);
        this.deterministic = true;
    }
    
    Operation(String mlog) {
        this.mlog = mlog;
        this.mindcode = List.of(mlog);
        this.deterministic = true;
    }

    Operation(boolean deterministic, String mlog) {
        this.mlog = mlog;
        this.mindcode = List.of(mlog);
        this.deterministic = deterministic;
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
            case ADD, MUL, LAND, OR, AND, XOR, MIN, MAX -> true;
            default -> false;
        };
    }

    public boolean isCommutative() {
        return switch(this) {
            case EQUAL, NOT_EQUAL, STRICT_EQUAL, ADD, MUL, LAND, OR, AND, XOR, MIN, MAX -> true;
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

    public boolean isInequalityOperator() {
        return switch(this) {
            case LESS_THAN, LESS_THAN_EQ, GREATER_THAN, GREATER_THAN_EQ -> true;
            default -> false;
        };
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
    
    private static final Map<String, Operation> MINDCODE_MAP = Stream.of(values())
            .flatMap(op -> op.mindcode.stream().map(mindcode -> new Tuple2<>(op, mindcode)))
            .collect(Collectors.toMap(Tuple2::getT2, Tuple2::getT1));
    private static final Map<String, Operation> MLOG_MAP = Stream.of(values()).collect(Collectors.toMap(Operation::toMlog, o -> o));
}
