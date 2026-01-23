package info.teksol.mc.mindcode.logic.arguments;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ContextFactory;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.PositionalMessage;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.instructions.ContextfulInstructionCreator;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.Consumer;

@NullMarked
public class LogicKeyword extends AbstractArgument implements ValueStore {
    public static final LogicKeyword BLOCK = create("block");
    public static final LogicKeyword ITEM = create("item");
    public static final LogicKeyword LIQUID = create("liquid");
    public static final LogicKeyword TEAM = create("team");
    public static final LogicKeyword UNIT = create("unit");
    public static final LogicKeyword INVALID = create("");

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

    public String getKeywordLiteral() {
        return ":" + keyword;
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
    public LogicValue getValue(ContextfulInstructionCreator creator) {
        ContextFactory.getMessageContext().addMessage(PositionalMessage.error(sourcePosition, ERR.INVALID_KEYWORD_USE));
        return LogicNull.NULL;
    }

    @Override
    public void readValue(ContextfulInstructionCreator creator, LogicVariable target) {
        ContextFactory.getMessageContext().addMessage(PositionalMessage.error(sourcePosition, ERR.INVALID_KEYWORD_USE));
    }

    @Override
    public void setValue(ContextfulInstructionCreator creator, LogicValue value) {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }

    @Override
    public void writeValue(ContextfulInstructionCreator creator, Consumer<LogicVariable> valueSetter) {
        throw new MindcodeInternalError("Unsupported for " + getClass().getSimpleName());
    }
}
