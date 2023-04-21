package info.teksol.mindcode.logic;

import info.teksol.mindcode.compiler.instructions.ReturnInstruction;

public enum LogicBoolean implements LogicLiteral {
    FALSE("false"),
    TRUE("true"),
    ;

    private final String mlog;

    LogicBoolean(String mlog) {
        this.mlog = mlog;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.BOOLEAN_LITERAL;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public LogicBoolean getInverse() {
        return this == FALSE ? TRUE : FALSE;
    }

    @Override
    public String format() {
        return this == TRUE ? "1" : "0";
    }
}
