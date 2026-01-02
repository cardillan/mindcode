package info.teksol.mc.emulator.v2;

import info.teksol.mc.emulator.MindustryObject;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.emulator.processor.TextBuffer;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_NOT_AN_OBJECT;
import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_UNSUPPORTED_BLOCK_OPERATION;

@NullMarked
public abstract class LExecutorBase extends AbstractMessageEmitter implements LExecutor {
    private final Set<ExecutionFlag> flags;
    protected final MindustryMetadata metadata;

    public static final int
            maxGraphicsBuffer = 256,
            maxDisplayBuffer = 1024,
            maxTextBuffer = 256;


    protected final TextBuffer textBuffer = new TextBuffer(10000, maxTextBuffer, false);
    protected List<MindustryBuilding> links = List.of();

    protected LInstruction[] instructions = {};

    /** Non-constant variables used for network sync */
    private LVar[] vars = {};

    protected LVar counter;
    protected LVar unit;
    protected LVar thisv;
    protected LVar ipt;

    public LExecutorBase(MessageConsumer messageConsumer, Set<ExecutionFlag> flags, MindustryMetadata metadata) {
        super(messageConsumer);
        this.flags = flags;
        this.metadata = metadata;
    }

    protected boolean getFlag(ExecutionFlag flag) {
        return flags.contains(flag);
    }

    protected boolean error(ExecutionFlag flag, @PrintFormat String message, Object... args) {
        if (flags.contains(flag)) {
            error(message, args);
            return false;
        } else {
            return true;
        }
    }

    // Note: statement building is performed by the LStatement/LStatements classes in Mindustry. As the emulator doesn't
    // implement this layer, the instruction building is contained here, as the executor needs to create the proper
    // version of each instruction.

    // Child classes populate instruction builders in constructor.
    protected Map<String, BiFunction<LAssembler, LStatement, LInstruction>> builders = new HashMap<>();

    @Override
    public LInstruction build(LAssembler assembler, LStatement statement) {
        return builders.getOrDefault(statement.opcode(), UnknownI::new).apply(assembler, statement);
    }

    @Override
    public TextBuffer textBuffer() {
        return textBuffer;
    }

    @Override
    public @Nullable MindustryBuilding getLink(int index) {
        return index >= 0 && index < links.size() ? links.get(index) : null;
    }

    @Override
    public LInstruction[] instructions() {
        return instructions;
    }

    @Override
    public LVar[] vars() {
        return vars;
    }

    @Override
    public LVar counter() {
        return counter;
    }

    @Override
    public LVar unit() {
        return unit;
    }

    @Override
    public LVar thisv() {
        return thisv;
    }

    @Override
    public LVar ipt() {
        return ipt;
    }

    protected static boolean invalid(double d){
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    protected <T extends MindustryObject> boolean blockOperation(String description, LVar variable, Class<T> type,
            Consumer<T> operation) {
        return blockOperation(description, variable, type, operation, () -> {});
    }

    protected <T extends MindustryObject> boolean blockOperation(String description, LVar variable, Class<T> type,
            Consumer<T> operation, Runnable onError) {
        Object obj = variable.obj();
        if (obj == null) {
            onError.run();
            return error(ERR_NOT_AN_OBJECT, "Variable is not an object: %s", variable);
        } else if (!type.isInstance(variable.getObject())) {
            onError.run();
            return error(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation '%s' on '%s' (class %s).",
                    description, variable.name, obj.getClass().getSimpleName());
        } else {
            //noinspection unchecked
            operation.accept((T) variable.getObject());
            return true;
        }
    }

    private static Object convertArgument(LAssembler assembler, String type, String symbol) {
        return "var".equals(type) ? assembler.var(symbol) : symbol;
    }

    protected static class UnknownI implements LInstruction {
        private final List<Object> arguments;

        public UnknownI(LAssembler assembler, LStatement stmt) {
            arguments = IntStream.of(stmt.types().size())
                    .mapToObj(i -> convertArgument(assembler, stmt.types().get(i), stmt.arguments().get(i)))
                    .toList();
        }

        @Override
        public boolean run() {
            // Do nothing
            return true;
        }
    }
}
