package info.teksol.mc.emulator.v2;

import info.teksol.mc.emulator.MindustryObject;
import info.teksol.mc.emulator.blocks.MemoryBlock;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.evaluator.ConditionEvaluator;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicCondition;
import info.teksol.mc.evaluator.LogicOperation;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.arguments.Operation;
import info.teksol.mc.mindcode.logic.mimex.LAccess;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_NOT_A_NUMBER;
import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_UNSUPPORTED_OPCODE;

@NullMarked
public class V6Executor extends LExecutorBase {

    public V6Executor(MessageConsumer messageConsumer, Set<ExecutionFlag> flags) {
        super(messageConsumer, flags, MindustryMetadata.forVersion(ProcessorVersion.V6));

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
    }

    protected class EndI implements LInstruction {
        public EndI(LAssembler assembler, LStatement statement) {
        }

        @Override
        public boolean run() {
            counter.numval = instructions.length;
            return !getFlag(ExecutionFlag.STOP_ON_PROGRAM_END);
        }
    }

    protected class GetLinkI implements LInstruction {
        private final LVar output, index;

        public GetLinkI(LAssembler assembler, LStatement statement) {
            output = assembler.var(statement.arg0());
            index = assembler.var(statement.arg1());
        }

        @Override
        public boolean run() {
            if (index.invalidNumber()) {
                output.setobj(null);
                return error(ERR_NOT_A_NUMBER, "Invalid numeric value in getlink index '%s'.", index);
            } else if (index.numi() < 0 || index.numi() >= links.size()) {
                 output.setobj(null);
                 return error(ExecutionFlag.ERR_INVALID_LINK, "Invalid link index %d.", index.numi());
            }  else {
                output.setobj(getLink(index.numi()));
                return true;
            }
        }
    }

    protected class JumpI implements LInstruction {
        private final int address;
        private final @Nullable LogicCondition condition;
        private final LVar value;
        private final LVar compare;

        public JumpI(LAssembler assembler, LStatement statement) {
            if (!(statement instanceof JumpStatement jump)) {
                throw new IllegalArgumentException("Expected JumpStatement, got " + statement.getClass().getName());
            }

            address = jump.destIndex;
            condition = ConditionEvaluator.getCondition(Condition.valueOf(statement.arg1()));
            value = assembler.var(statement.arg2());
            compare = assembler.var(statement.arg3());
        }

        @Override
        public boolean run() {
            if (address == -1) {
                return error(ERR_UNSUPPORTED_OPCODE, "Invalid jump address.");
            } else if (condition == null) {
                return error(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition.");
            } else if (condition.evaluate(value, compare)) {
                counter.numval = address;
            }
            return true;
        }
    }

    protected class OperationI implements LInstruction {
        private final @Nullable LogicOperation operation;
        private final LVar dest;
        private final LVar a;
        private final LVar b;

        public OperationI(LAssembler assembler, LStatement statement) {
            operation = ExpressionEvaluator.getOperation(Operation.valueOf(statement.arg0()));
            dest = assembler.var(statement.arg1());
            a = assembler.var(statement.arg2());
            b = assembler.var(statement.arg3());
        }

        @Override
        public boolean run() {
            if (operation == null) {
                return error(ERR_UNSUPPORTED_OPCODE, "Invalid operation.");
            } else {
                operation.execute(dest, a, b);
                return true;
            }
        }
    }

    protected class PrintI implements LInstruction {
        private final LVar value;

        public PrintI(LAssembler assembler, LStatement statement) {
            value = assembler.var(statement.arg0());
        }

        @Override
        public boolean run() {
            if (value.isobj) {
                String strValue = toString(value.objval);
                textBuffer.print(strValue);
            } else {
                //display integer version when possible
                if (Math.abs(value.numval - Math.round(value.numval)) < 0.00001) {
                    textBuffer.print(String.valueOf(Math.round(value.numval)));
                } else {
                    textBuffer.print(String.valueOf(value.numval));
                }
            }
            return true;
        }

        public static String toString(@Nullable Object obj) {
            return switch (obj) {
                case null -> "null";
                case String s -> s;
                case MindustryContent c -> c.contentName();
                case MindustryBuilding b -> b.type().contentName();     // b.type() will be non-null eventually
                default -> "[object]";
            };

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

    protected class PrintFlushI implements LInstruction {
        private final LVar message;

        public PrintFlushI(LAssembler assembler, LStatement statement) {
            message = assembler.var(statement.arg0());
        }

        @Override
        public boolean run() {
            if (message.getObject() == null) {
                textBuffer.printflush(null);
                return true;
            } else {
                return blockOperation("printflush", message, MessageBlock.class,
                        message -> message.printflush(textBuffer),
                        () -> textBuffer.printflush(null));
            }
        }
    }

    protected class ReadI implements LInstruction {
        private final LVar output;
        private final LVar target;
        private final LVar address;

        public ReadI(LAssembler assembler, LStatement statement) {
            output = assembler.var(statement.arg0());
            target = assembler.var(statement.arg1());
            address = assembler.var(statement.arg2());
        }

        @Override
        public boolean run() {
            return blockOperation("read", target, MemoryBlock.class,
                    memory -> output.setnum(memory.read(address.numi())));
        }
    }

    protected class SensorI implements LInstruction {
        private final LVar to;
        private final LVar from;
        private final LVar type;

        public SensorI(LAssembler assembler, LStatement statement) {
            to = assembler.var(statement.arg0());
            from = assembler.var(statement.arg1());
            type = assembler.var(statement.arg2());
        }

        @Override
        public boolean run() {
            Object target = from.obj();
            Object sense = type.obj();

            if (target == null) {
                if (sense instanceof LAccess access && access.name().equals("@dead")) {
                    to.setnum(1);
                } else {
                    to.setobj(null);
                }
                return true;
            }

            if (target instanceof MindustryObject inner && sense instanceof LAccess access) {
                switch (access.name()) {
                    case "@type" -> {
                        to.setobj(inner.type());
                        return true;
                    }
                    case "@name" -> {
                        to.setobj(inner instanceof MindustryContent content ? content.contentName() : null);
                        return true;
                    }
                }
            }

            return error(ERR_UNSUPPORTED_OPCODE, "Instruction not supported by Mindcode emulator.");
        }
    }

    protected class SetI implements LInstruction {
        private final LVar result;
        private final LVar source;

        public SetI(LAssembler assembler, LStatement statement) {
            result = assembler.var(statement.arg0());
            source = assembler.var(statement.arg1());
        }

        @Override
        public boolean run() {
            if (!result.constant) {
                if (source.isobj) {
                    result.objval = source.objval;
                    result.isobj = true;
                } else {
                    result.numval = invalid(source.numval) ? 0 : source.numval;
                    result.isobj = false;
                }
            }
            return true;
        }
    }

    protected class WriteI implements LInstruction {
        private final LVar input;
        private final LVar target;
        private final LVar address;

        public WriteI(LAssembler assembler, LStatement statement) {
            input = assembler.var(statement.arg0());
            target = assembler.var(statement.arg1());
            address = assembler.var(statement.arg2());
        }

        @Override
        public boolean run() {
            return blockOperation("read", target, MemoryBlock.class,
                    memory -> memory.write(address.numi(), input.numi()));
        }
    }
}
