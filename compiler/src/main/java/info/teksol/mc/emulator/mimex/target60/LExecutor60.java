package info.teksol.mc.emulator.mimex.target60;

import info.teksol.mc.emulator.ExecutionFlag;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.MindustryObject;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MemoryBlock;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.mimex.*;
import info.teksol.mc.evaluator.ConditionEvaluator;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicCondition;
import info.teksol.mc.evaluator.LogicOperation;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.mimex.LAccess;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static info.teksol.mc.emulator.ExecutionFlag.*;

@NullMarked
public class LExecutor60 extends LExecutorBase {
    protected final List<ReadWriteHandler> readHandlers = new ArrayList<>();
    protected final List<ReadWriteHandler> writeHandlers = new ArrayList<>();

    protected interface ReadWriteHandler {
        boolean handle(LVar variable, Object object, LVar address);
    }

    public LExecutor60(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        super(metadata, assembler, emulator, logicBlock);

        builders.put("draw", DrawI::new);
        builders.put("drawflush", DrawFlushI::new);
        builders.put("end", EndI::new);
        builders.put("getlink", GetLinkI::new);
        builders.put("jump", JumpI::new);
        builders.put("op", OperationI::new);
        builders.put("print", PrintI::new);
        builders.put("printflush", PrintFlushI::new);
        builders.put("read", ReadI::new);
        builders.put("sensor", SensorI::new);
        builders.put("set", SetI::new);
        builders.put("write", WriteI::new);

        readHandlers.add(this::memoryRead);
        writeHandlers.add(this::memoryWrite);
    }

    protected boolean memoryRead(LVar output, Object object, LVar address) {
        if (!(object instanceof MemoryBlock memory)) {
            return false;
        }

        if (address.invalidNumber()) {
            error(ERR_NOT_A_NUMBER, "Invalid numeric value in read address '%s'.", address);
        }

        int index = address.numi();
        if (index < 0 || index >= memory.size()) {
            error(ERR_MEMORY_ACCESS, "Memory access out of bounds: index %d, memory size %d.",
                    index, memory.size());
            setVar(output, 0);
        } else {
            setVar(output, memory.read(index));
        }

        return true;
    }

    protected boolean memoryWrite(LVar input, Object object, LVar address) {
        if (!(object instanceof MemoryBlock memory)) {
            return false;
        }

        if (address.invalidNumber()) {
            error(ERR_NOT_A_NUMBER, "Invalid numeric value in write address '%s'.", address);
        }

        if (input.isobj && input.objval != null) {
            error(ERR_MEMORY_OBJECT, "Attempting to store an object in memory: %s.", input);
        }

        int index = address.numi();
        if (index < 0 || index >= memory.size()) {
            error(ERR_MEMORY_ACCESS, "Memory access out of bounds: index %d, memory size %d.",
                    index, memory.size());
        } else {
            memory.write(index, input.num());
        }

        return true;
    }


    protected String formatDouble(double value) {
        if (Math.abs(value - (long) value) < 0.00001) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value);
        }
    }

    protected class DrawI extends AbstractInstruction {
        protected final String command;

        public DrawI(LStatement statement) {
            super(statement);
            command = statement.arg0();
        }

        @Override
        public void run() {
            // TODO: update graphicsBuffer

            if ("print".equals(command)) {
                textBuffer.printflush(null);
            }
        }
    }

    protected class DrawFlushI extends AbstractInstruction {
        public DrawFlushI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            // TODO: update graphicsBuffer
        }
    }

    protected class EndI extends AbstractInstruction {
        public EndI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            counter.numval = instructions.size();
            finish(STOP_ON_END_INSTRUCTION);
        }
    }

    protected class GetLinkI extends AbstractInstruction {
        protected final LVar output, index;

        public GetLinkI(LStatement statement) {
            super(statement);
            output = assembler.var(statement.arg0());
            index = assembler.var(statement.arg1());
        }

        @Override
        public void run() {
            if (index.invalidNumber()) {
                error(ERR_NOT_A_NUMBER, "Invalid numeric value in getlink index '%s'.", index);
            }

            int value = index.numi();
            if (value < 0 || value >= linkedBlocks.size()) {
                error(ExecutionFlag.ERR_INVALID_LINK, "Invalid link index %d.", value);
            }

            setVar(output, getLink(value));
        }
    }

    protected class JumpI extends AbstractInstruction {
        protected final int address;
        protected final @Nullable LogicCondition condition;
        protected final LVar value;
        protected final LVar compare;

        public JumpI(LStatement statement) {
            super(statement);
            if (!(statement instanceof JumpStatement jump)) {
                throw new IllegalArgumentException("Expected JumpStatement, got " + statement.getClass().getName());
            }

            address = jump.destIndex;
            condition = ConditionEvaluator.getCondition(Condition.fromMlog(statement.arg1()));
            value = assembler.var(statement.arg2());
            compare = assembler.var(statement.arg3());
        }

        @Override
        public void run() {
            if (address == -1) {
                error(ERR_UNSUPPORTED_OPCODE, "Invalid jump address.");
            } else if (condition == null) {
                error(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition.");
            } else if (condition.evaluate(value, compare)) {
                counter.numval = address;
            }
        }
    }

    protected class OperationI extends AbstractInstruction {
        protected final @Nullable LogicOperation operation;
        protected final LVar dest;
        protected final LVar a;
        protected final LVar b;

        public OperationI(LStatement statement) {
            super(statement);
            operation = ExpressionEvaluator.getOperation(Operation.fromMlog(statement.arg0()));
            dest = assembler.var(statement.arg1());
            a = assembler.var(statement.arg2());
            b = assembler.var(statement.arg3());
        }

        @Override
        public void run() {
            if (operation == null) {
                error(ERR_UNSUPPORTED_OPCODE, "Invalid operation.");
            } else if (checkWritable(dest)) {
                operation.execute(dest, a, b);
            }
        }
    }

    protected class PrintI extends AbstractInstruction {
        protected final LVar value;

        public PrintI(LStatement statement) {
            super(statement);
            value = assembler.var(statement.arg0());
        }

        @Override
        public void run() {
            textBuffer.print(formatValue());
        }

        public String formatValue() {
            if (value.isobj) {
                return switch (value.objval) {
                    case null -> "null";
                    case String s -> s;
                    case MindustryObject c -> c.format();
                    default -> "[object]";
                };
            } else {
                return formatDouble(value.numval);
            }
            //return
            //    obj == null ? "null" :
            //    obj instanceof String s ? s :
            //    obj instanceof MappableContent content ? content.name :
            //    obj instanceof Content ? "[content]" :
            //    obj instanceof Building build ? build.block.name :
            //    obj instanceof Unit unit ? unit.type.name :
            //    obj instanceof Enum<?> e ? e.name() :
            //    obj instanceof Team team ? team.name :
            //    "[object]";
        }
    }

    protected class PrintFlushI extends AbstractInstruction {
        protected final LVar message;

        public PrintFlushI(LStatement statement) {
            super(statement);
            message = assembler.var(statement.arg0());
        }

        @Override
        public void run() {
            Object obj = message.obj();
            if (obj == null) {
                textBuffer.printflush(null);
            } else {
                if (obj instanceof MessageBlock msg) {
                    msg.printflush(textBuffer);
                } else {
                    textBuffer.printflush(null);
                    error(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation 'printflush' on '%s' (class %s).",
                            message, obj.getClass().getSimpleName());
                }
            }
        }
    }

    protected class ReadI extends AbstractInstruction {
        protected final LVar output;
        protected final LVar target;
        protected final LVar address;

        public ReadI(LStatement statement) {
            super(statement);
            output = assembler.var(statement.arg0());
            target = assembler.var(statement.arg1());
            address = assembler.var(statement.arg2());
        }

        @Override
        public void run() {
            Object object = target.obj();

            if (object == null) {
                // Null
                error(ERR_NOT_AN_OBJECT, "Variable is not an object: %s", target);
            } else {
                for (ReadWriteHandler handler : readHandlers) {
                    if (handler.handle(output, object, address)) {
                        return;
                    }
                }

                // Non-null, but unexpected
                error(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation 'read' on '%s' (class %s).",
                        target, object.getClass().getSimpleName());
            }
        }
    }

    protected class SensorI extends AbstractInstruction {
        protected final LVar to;
        protected final LVar from;
        protected final LVar type;

        public SensorI(LStatement statement) {
            super(statement);
            to = assembler.var(statement.arg0());
            from = assembler.var(statement.arg1());
            type = assembler.var(statement.arg2());
        }

        @Override
        public void run() {
            Object target = from.obj();
            Object sense = type.obj();

            checkWritable(to);

            if (target == null) {
                if (sense instanceof LAccess access && access.name().equals("@dead")) {
                    setVar(to, 1);
                } else {
                    setNull(to);
                }
                return;
            }

            if (target instanceof MindustryObject inner && sense instanceof LAccess access && handleLAccess(inner, access)) {
                return;
            }

            error(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
        }

        protected boolean handleLAccess(MindustryObject object, LAccess access) {
            switch (access.name()) {
                case "@type" -> {
                    setVar(to, object.type());
                    return true;
                }
                case "@id" -> {
                    // Note: in target 6, the "@id" LAccess doesn't exist and this branch will never get executed
                    if (object instanceof MindustryContent content && content.logicId() >= 0) {
                        setVar(to, content.logicId());
                    } else {
                        // §§§ Report error?
                        setVar(to, null);
                    }
                    return true;
                }
                case "@name" -> {
                    // §§§ Report error?
                    setVar(to, object instanceof MindustryContent content ? content.contentName() : null);
                    return true;
                }
                case "@x" -> {
                    // §§§ Report error?
                    setVar(to, object instanceof MindustryBuilding building ? building.x() : null);
                    return true;
                }
                case "@y" -> {
                    // §§§ Report error?
                    setVar(to, object instanceof MindustryBuilding building ? building.y() : null);
                    return true;
                }
            }
            return false;
        }
    }

    public class SetI extends AbstractInstruction {
        public final LVar result;
        public final LVar source;

        public SetI(LStatement statement) {
            super(statement);
            result = assembler.var(statement.arg0());
            source = assembler.var(statement.arg1());
        }

        @Override
        public void run() {
            copyVar(result, source);
        }
    }

    protected class WriteI extends AbstractInstruction {
        protected final LVar input;
        protected final LVar target;
        protected final LVar address;

        public WriteI(LStatement statement) {
            super(statement);
            input = assembler.var(statement.arg0());
            target = assembler.var(statement.arg1());
            address = assembler.var(statement.arg2());
        }

        @Override
        public void run() {
            Object object = target.obj();

            if (object == null) {
                // Null
                error(ERR_NOT_AN_OBJECT, "Variable is not an object: %s", target);
            } else {
                for (ReadWriteHandler handler : writeHandlers) {
                    if (handler.handle(input, object, address)) {
                        return;
                    }
                }

                // Non-null, but unexpected
                error(ERR_UNSUPPORTED_BLOCK_OPERATION, "Unsupported operation 'write' on '%s' (class %s).",
                        target, object.getClass().getSimpleName());
            }
        }
    }
}
