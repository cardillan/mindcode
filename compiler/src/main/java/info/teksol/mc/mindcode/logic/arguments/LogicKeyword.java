package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.generation.CodeAssembler;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class LogicKeyword extends AbstractArgument implements ValueStore {
    private final SourcePosition sourcePosition;
    private final String keyword;

    private LogicKeyword(SourcePosition sourcePosition, String keyword) {
        super(ArgumentType.KEYWORD, ValueMutability.IMMUTABLE);
        this.sourcePosition = sourcePosition;
        this.keyword = Objects.requireNonNull(keyword);
    }

    @Override
    public SourcePosition sourcePosition() {
        return sourcePosition;
    }

    public String getKeyword() {
        return keyword;
    }

    @Override
    public String toMlog() {
        return keyword;
    }

    @Override
    public String toString() {
        return "LogicKeyword{" +
                "keyword='" + keyword + '\'' +
                '}';
    }

    public static LogicKeyword create(SourcePosition sourcePosition, String keyword) {
        return new LogicKeyword(sourcePosition, keyword);
    }

    public static LogicKeyword create(String keyword) {
        return new LogicKeyword(SourcePosition.EMPTY, keyword);
    }

    public static final LogicKeyword INVALID = create("");

    @Override
    public boolean isMlogRepresentable() {
        return false;
    }

    @Override
    public boolean isComplex() {
        // This class can never be used in a context where isComplex matters.
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    public boolean isLvalue() {
        return false;
    }

    @Override
    public LogicValue getValue(CodeAssembler assembler) {
        assembler.error(sourcePosition, ERR.INVALID_KEYWORD_USE);
        return LogicNull.NULL;
    }

    @Override
    public void readValue(CodeAssembler assembler, LogicVariable target) {
        assembler.error(sourcePosition, ERR.INVALID_KEYWORD_USE);
    }

    @Override
    public void setValue(CodeAssembler assembler, LogicValue value) {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    public void writeValue(CodeAssembler assembler, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }
}
