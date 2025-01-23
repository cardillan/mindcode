package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum Condition implements LogicArgument {
    EQUAL           ( "equal",         "==",     true),
    NOT_EQUAL       ( "notEqual",      "not",    false),
    LESS_THAN       ( "lessThan",      "<",      false),
    LESS_THAN_EQ    ( "lessThanEq",    "<=",     true),
    GREATER_THAN    ( "greaterThan",   ">",      false),
    GREATER_THAN_EQ ( "greaterThanEq", ">=",     true),
    STRICT_EQUAL    ( "strictEqual",   "===",    true),
    ALWAYS          ( "always",        "always", false);


    private final String mlog;
    private final String mindcode;
    private final boolean equality;

    Condition(String mlog, String mindcode, boolean equality) {
        this.mindcode = mindcode;
        this.mlog = mlog;
        this.equality = equality;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.KEYWORD;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.IMMUTABLE;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public String getMindcode() {
        return mindcode;
    }

    public boolean hasInverse() {
        return this != ALWAYS && this != STRICT_EQUAL;
    }

    public boolean isEquality() {
        return equality;
    }

    public Condition inverse() {
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

    public @Nullable Operation toOperation() {
        return switch (this) {
            case EQUAL -> Operation.EQUAL;
            case NOT_EQUAL -> Operation.NOT_EQUAL;
            case LESS_THAN -> Operation.LESS_THAN;
            case LESS_THAN_EQ -> Operation.LESS_THAN_EQ;
            case GREATER_THAN -> Operation.GREATER_THAN;
            case GREATER_THAN_EQ -> Operation.GREATER_THAN_EQ;
            case STRICT_EQUAL -> Operation.STRICT_EQUAL;
            default -> null;
        };
    }
}
