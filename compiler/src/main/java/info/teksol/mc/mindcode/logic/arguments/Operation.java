package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public enum Operation implements LogicArgument {
    EQUAL           (2, true, "equal",         MindcodeLexer.EQUAL),
    NOT_EQUAL       (2, true, "notEqual",      MindcodeLexer.NOT_EQUAL),
    LESS_THAN       (2, true, "lessThan",      MindcodeLexer.LESS_THAN),
    LESS_THAN_EQ    (2, true, "lessThanEq",    MindcodeLexer.LESS_THAN_EQUAL),
    GREATER_THAN    (2, true, "greaterThan",   MindcodeLexer.GREATER_THAN),
    GREATER_THAN_EQ (2, true, "greaterThanEq", MindcodeLexer.GREATER_THAN_EQUAL),
    STRICT_EQUAL    (2, true, "strictEqual",   MindcodeLexer.STRICT_EQUAL),
    STRICT_NOT_EQUAL(2, true, null,            MindcodeLexer.STRICT_NOT_EQUAL),

    ADD             (2, true, "add",  MindcodeLexer.PLUS),
    SUB             (2, true, "sub",  MindcodeLexer.MINUS),
    MUL             (2, true, "mul",  MindcodeLexer.MUL),
    DIV             (2, true, "div",  MindcodeLexer.DIV),
    IDIV            (2, true, "idiv", MindcodeLexer.IDIV),
    MOD             (2, true, "mod",  MindcodeLexer.MOD),
    POW             (2, true, "pow",  MindcodeLexer.POW),
    SHL             (2, true, "shl",  MindcodeLexer.SHIFT_LEFT),
    SHR             (2, true, "shr",  MindcodeLexer.SHIFT_RIGHT),

    BITWISE_AND     (2, true, "and",  MindcodeLexer.BITWISE_AND),
    BITWISE_OR      (2, true, "or",   MindcodeLexer.BITWISE_OR),
    BITWISE_XOR     (2, true, "xor",  MindcodeLexer.BITWISE_XOR),
    BITWISE_NOT     (1, true, "not",  MindcodeLexer.BITWISE_NOT),
    // Boolean: guaranteed to produce 0/1.
    BOOLEAN_AND     (2, true, "land", MindcodeLexer.BOOLEAN_AND),
    BOOLEAN_OR      (2, true, "or",   MindcodeLexer.BOOLEAN_OR),
    BOOLEAN_NOT     (1, true, null,   MindcodeLexer.BOOLEAN_NOT),
    // Logical: produce null/zero or nonzero, can be short-circuited.
    LOGICAL_AND     (2, true, "land", MindcodeLexer.LOGICAL_AND),
    LOGICAL_OR      (2, true, "or",   MindcodeLexer.LOGICAL_OR),
    LOGICAL_NOT     (1, true, null,   MindcodeLexer.LOGICAL_NOT),

    // Functions
    MAX             (2, true,  "max"),
    MIN             (2, true,  "min"),
    ANGLE           (2, true,  "angle"),
    ANGLEDIFF       (2, true,  "angleDiff"),
    LEN             (2, true,  "len"),
    NOISE           (2, false, "noise"),
    ABS             (1, true,  "abs"),
    SIGN            (1, true,  "sign"),
    LOG             (1, true,  "log"),
    LOG10           (1, true,  "log10"),
    FLOOR           (1, true,  "floor"),
    CEIL            (1, true,  "ceil"),
    SQRT            (1, true,  "sqrt"),
    RAND            (1, false, "rand"),
    SIN             (1, true,  "sin"),
    COS             (1, true,  "cos"),
    TAN             (1, true,  "tan"),
    ASIN            (1, true,  "asin"),
    ACOS            (1, true,  "acos"),
    ATAN            (1, true,  "atan"),
    ;

    private final @Nullable String mlog;
    private final String mindcode;
    private final int token;
    private final int operands;
    private final boolean deterministic;
    private final boolean function;

    Operation(int arity, boolean deterministic, @Nullable String mlog, int token) {
        this.mlog = mlog;
        String literalName = MindcodeLexer.VOCABULARY.getLiteralName(token);
        this.mindcode = literalName.substring(1, literalName.length() - 1);
        this.token = token;
        this.operands = arity;
        this.deterministic = deterministic;
        this.function = false;
    }

    Operation(int arity, boolean deterministic, String mlog) {
        this.mlog = mlog;
        this.mindcode = mlog;
        this.token = -1;
        this.operands = arity;
        this.deterministic = deterministic;
        this.function = true;
    }

    public static @Nullable Operation fromMindcode(String code) {
        return MINDCODE_MAP.get(code);
    }

    public static @Nullable Operation fromMlog(String name) {
        return MLOG_MAP.get(name);
    }

    public static Operation fromToken(int tokenType) {
        return Objects.requireNonNull(TOKENS.get(tokenType), "Unknown or invalid token " + tokenType);
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.KEYWORD;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.IMMUTABLE;
    }

    public int getOperands() {
        return operands;
    }

    @Override
    public String toMlog() {
        if (mlog == null) {
            throw new MindcodeInternalError("No mlog representation for operation " + this);
        }
        return mlog;
    }

    public String getMindcode() {
        return mindcode;
    }

    public boolean isFunction() {
        return function;
    }

    public boolean isCondition() {
        return ordinal() <= STRICT_EQUAL.ordinal();
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public boolean isAssociative() {
        return switch(this) {
            case BITWISE_AND, BITWISE_OR, BOOLEAN_AND, BOOLEAN_OR, LOGICAL_AND, LOGICAL_OR, ADD, MUL, BITWISE_XOR, MIN, MAX -> true;
            default -> false;
        };
    }

    public boolean isCommutative() {
        return switch(this) {
            case EQUAL, NOT_EQUAL, STRICT_EQUAL, BITWISE_AND, BITWISE_OR, BOOLEAN_AND, BOOLEAN_OR, LOGICAL_AND, LOGICAL_OR, ADD, MUL,
                 BITWISE_XOR, MIN, MAX -> true;
            default -> false;
        };
    }

    public @Nullable Condition toCondition() {
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

    public Condition toExistingCondition() {
        return Objects.requireNonNull(toCondition(), "Operation " + this + " is not a condition");
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

    public @Nullable Operation swapped() {
        return switch(this) {
            case LESS_THAN -> GREATER_THAN;
            case LESS_THAN_EQ -> GREATER_THAN_EQ;
            case GREATER_THAN -> LESS_THAN;
            case GREATER_THAN_EQ -> LESS_THAN_EQ;
            default -> null;
        };
    }

    private static final Map<String, Operation> MINDCODE_MAP = Stream.of(values())
            .filter(o -> o.mlog != null)
            .collect(Collectors.toMap(Operation::getMindcode, o -> o));

    private static final Map<String, Operation> MLOG_MAP = Stream.of(values())
            .filter(o -> o.mlog != null)
            .collect(Collectors.toMap(Operation::toMlog, o -> o, (o1, o2) -> o1));

    private static final List<@Nullable Operation> TOKENS = new ArrayList<>();
    static {
        int tokens = MindcodeLexer.VOCABULARY.getMaxTokenType();
        TOKENS.addAll(Collections.nCopies(tokens, null));
        for (Operation op : Operation.values()) {
            if (op.token >= 0) {
                TOKENS.set(op.token, op);
            }
        }
    }
}
