package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstLiteralBoolean;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum LogicBoolean implements LogicLiteral {
    FALSE("false"),
    TRUE("true"),
    ;

    private final String mlog;

    LogicBoolean(String mlog) {
        this.mlog = mlog;
    }

    @Override
    public SourcePosition sourcePosition() {
        return SourcePosition.EMPTY;
    }

    @Override
    public ArgumentType getType() {
        return ArgumentType.BOOLEAN_LITERAL;
    }

    @Override
    public ValueMutability getMutability() {
        return ValueMutability.CONSTANT;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public double getDoubleValue() {
        return this == TRUE ? 1.0 : 0.0;
    }

    @Override
    public long getLongValue() {
        return this == TRUE ? 1 : 0;
    }

    @Override
    public @Nullable Object getObject() {
        return null;
    }

    @Override
    public boolean isNumericLiteral() {
        return true;
    }

    @Override
    public String toMlog() {
        return mlog;
    }

    public LogicBoolean not() {
        return this == FALSE ? TRUE : FALSE;
    }

    @Override
    public String format(@Nullable InstructionProcessor instructionProcessor) {
        return this == TRUE ? "1" : "0";
    }

    public static LogicBoolean get(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public AstMindcodeNode asAstNode(SourcePosition position) {
        return new AstLiteralBoolean(position, mlog);
    }
}
