package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.profile.GlobalCompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum Condition implements LogicArgument {
    EQUAL           ( "equal",         "==",     true,  true),
    NOT_EQUAL       ( "notEqual",      "not",    true,  false),
    LESS_THAN       ( "lessThan",      "<",      false, false),
    LESS_THAN_EQ    ( "lessThanEq",    "<=",     false, true),
    GREATER_THAN    ( "greaterThan",   ">",      false, false),
    GREATER_THAN_EQ ( "greaterThanEq", ">=",     false, true),
    STRICT_EQUAL    ( "strictEqual",   "===",    true,  true),
    STRICT_NOT_EQUAL( "strictNotEqual","!==",    true,  false),
    ALWAYS          ( "always",        "always", false, true);


    private final String mlog;
    private final String mindcode;
    private final boolean equality;
    private final boolean reflexive;

    Condition(String mlog, String mindcode, boolean equality, boolean reflexive) {
        this.mindcode = mindcode;
        this.mlog = mlog;
        this.equality = equality;
        this.reflexive = reflexive;
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

    public boolean isEquality() {
        return equality;
    }

    public boolean isCommutative() {
        return isEquality();
    }

    public boolean isReflexive() {
        return reflexive;
    }

    public boolean hasInverse(boolean allowStrictNotEqual) {
        return this != ALWAYS && (allowStrictNotEqual || this != STRICT_EQUAL);
    }

    public boolean hasInverse(GlobalCompilerProfile profile) {
        return hasInverse(profile.useEmulatedStrictNotEqual());
    }

    public boolean isStrict() {
        return switch (this) {
            case STRICT_EQUAL, STRICT_NOT_EQUAL -> true;
            default -> false;
        };
    }

    public Condition opposite() {
        return switch (this) {
            case LESS_THAN -> GREATER_THAN;
            case GREATER_THAN_EQ -> LESS_THAN_EQ;
            case LESS_THAN_EQ -> GREATER_THAN_EQ;
            case GREATER_THAN -> LESS_THAN;
            default -> this;
        };
    }

    public Condition inverse(boolean allowStrictNotEqual) {
        return switch (this) {
            case EQUAL -> NOT_EQUAL;
            case NOT_EQUAL -> EQUAL;
            case LESS_THAN -> GREATER_THAN_EQ;
            case GREATER_THAN_EQ -> LESS_THAN;
            case LESS_THAN_EQ -> GREATER_THAN;
            case GREATER_THAN -> LESS_THAN_EQ;
            case STRICT_EQUAL -> {
                if (!allowStrictNotEqual) throw new MindcodeInternalError(this + " has no inverse.");
                yield STRICT_NOT_EQUAL;
            }
            case STRICT_NOT_EQUAL -> STRICT_EQUAL;
            default -> throw new MindcodeInternalError(this + " has no inverse.");
        };
    }

    public Condition inverse(GlobalCompilerProfile profile) {
        return inverse(profile.useEmulatedStrictNotEqual());
    }

    public Operation toOperation() {
        return switch (this) {
            case EQUAL -> Operation.EQUAL;
            case NOT_EQUAL -> Operation.NOT_EQUAL;
            case LESS_THAN -> Operation.LESS_THAN;
            case LESS_THAN_EQ -> Operation.LESS_THAN_EQ;
            case GREATER_THAN -> Operation.GREATER_THAN;
            case GREATER_THAN_EQ -> Operation.GREATER_THAN_EQ;
            case STRICT_EQUAL -> Operation.STRICT_EQUAL;
            case STRICT_NOT_EQUAL -> Operation.STRICT_NOT_EQUAL;
            case ALWAYS -> throw new MindcodeInternalError("No operation for 'always'");
        };
    }
}
