package info.teksol.mc.emulator.mimex.target81;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.emulator.mimex.LAssembler;
import info.teksol.mc.emulator.mimex.LStatement;
import info.teksol.mc.emulator.mimex.target80.LExecutor80;
import info.teksol.mc.evaluator.ConditionEvaluator;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicCondition;
import info.teksol.mc.mindcode.logic.arguments.Condition;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static info.teksol.mc.emulator.ExecutionFlag.ERR_NOT_A_NUMBER;
import static info.teksol.mc.emulator.ExecutionFlag.ERR_UNSUPPORTED_OPCODE;

@NullMarked
public class LExecutor81 extends LExecutor80 {

    public LExecutor81(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        super(metadata, assembler, emulator, logicBlock);
        yieldPreservesAccumulator = true;
        zeroWaitYields = true;

        builders.put("select", SelectI::new);
        builders.put("unpackcolor", UnpackColorI::new);

        readHandlers.add(this::messageRead);
    }

    @Override
    protected void copyRemoteVar(@Nullable LVar from, LVar to) {
        if (from == null) {
            setNull(to);
        } else {
            copyVar(to, from);
        }
    }

    protected boolean messageRead(LVar output, Object object, LVar address) {
        if (!(object instanceof MessageBlock message)) {
            return false;
        }
        if (address.invalidNumber()) {
            error(ERR_NOT_A_NUMBER, "Invalid numeric value in read address '%s'.", address);
        }

        // Reading outside string is not an error
        StringBuilder str = message.getMessage();
        int index = address.numi();
        setVar(output, index < 0 || index >= str.length() ? Double.NaN : (int) str.charAt(index));
        return true;
    }

    protected class SelectI extends AbstractInstruction {
        protected final LVar result;
        protected final @Nullable LogicCondition condition;
        protected final LVar value;
        protected final LVar compare;
        protected final LVar valueIfTrue;
        protected final LVar valueIfFalse;

        public SelectI(LStatement statement) {
            super(statement);
            result = assembler.var(statement.arg0());
            condition = ConditionEvaluator.getCondition(Condition.fromMlog(statement.arg1()));
            value = assembler.var(statement.arg2());
            compare = assembler.var(statement.arg3());
            valueIfTrue = assembler.var(statement.arg4());
            valueIfFalse = assembler.var(statement.arg5());
        }

        @Override
        public void run() {
            if (condition == null) {
                error(ERR_UNSUPPORTED_OPCODE, "Invalid jump condition.");
            } else {
                copyVar(result, condition.evaluate(value, compare) ? valueIfTrue : valueIfFalse);
            }
        }
    }

    protected class UnpackColorI extends AbstractInstruction {
        protected final LVar r, g, b, a, color;

        public UnpackColorI(LStatement statement) {
            super(statement);
            r = assembler.var(statement.arg0());
            g = assembler.var(statement.arg1());
            b = assembler.var(statement.arg2());
            a = assembler.var(statement.arg3());
            color = assembler.var(statement.arg4());
        }

        @Override
        public void run() {
            checkWritable(r);
            checkWritable(g);
            checkWritable(b);
            checkWritable(a);
            ExpressionEvaluator.evaluateUnpackColor(color, r, g, b, a);
        }
    }
}
