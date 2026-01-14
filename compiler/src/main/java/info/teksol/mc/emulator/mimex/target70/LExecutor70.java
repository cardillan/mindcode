package info.teksol.mc.emulator.mimex.target70;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.emulator.mimex.LAssembler;
import info.teksol.mc.emulator.mimex.LStatement;
import info.teksol.mc.emulator.mimex.target60.LExecutor60;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static info.teksol.mc.emulator.ExecutionFlag.*;

@NullMarked
public class LExecutor70 extends LExecutor60 {

    public LExecutor70(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        super(metadata, assembler, emulator, logicBlock);

        builders.put("lookup", LookupI::new);
        builders.put("packcolor", PackColorI::new);
        builders.put("stop", StopI::new);
        builders.put("wait", WaitI::new);

        builders.put("setrate", SetRateI::new);
    }

    protected class LookupI extends AbstractInstruction {
        protected final String type;
        @Nullable Map<Integer, ? extends MindustryContent> lookupMap;
        protected final LVar result, id;

        public LookupI(LStatement statement) {
            super(statement);
            type = statement.arg0();
            lookupMap = metadata.getLookupMap(type);
            result = assembler.var(statement.arg1());
            id = assembler.var(statement.arg2());
        }

        @Override
        public void run() {
            if (lookupMap == null) {
                error(ERR_UNSUPPORTED_OPCODE, "Invalid lookup type '%s'.", type);
            } else {
                MindustryContent content = lookupMap.get(id.numi());

                if (id.invalidNumber()) {
                    error(ERR_NOT_A_NUMBER, "Invalid numeric value in lookup index %s ", id);
                } else if (content == null) {
                    error(ERR_INVALID_LOOKUP, "Invalid lookup index %d for type '%s'.", id.numi(), type);
                }

                setVar(result, content);
            }
        }
    }

    protected class PackColorI extends AbstractInstruction {
        protected final LVar result, r, g, b, a;

        public PackColorI(LStatement statement) {
            super(statement);
            result = assembler.var(statement.arg0());
            r = assembler.var(statement.arg1());
            g = assembler.var(statement.arg2());
            b = assembler.var(statement.arg3());
            a = assembler.var(statement.arg4());
        }

        @Override
        public void run() {
            if (checkWritable(result)) {
                ExpressionEvaluator.evaluatePackColor(result, r, g, b, a);
            }
        }
    }

    protected class StopI extends AbstractInstruction {

        public StopI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            counter.numval--;
            if (errorHandler.trace(DUMP_VARIABLES_ON_STOP)) {
                errorHandler.info("\n'stop' instruction encountered, dumping variable values:");
                vars.values().stream()
                        .filter(v -> !v.constant)
                        .map(v -> v.name + ": " + v.printExact())
                        .forEach(errorHandler::info);
            }
            finish(STOP_ON_STOP_INSTRUCTION);
        }
    }

    protected class WaitI extends AbstractInstruction {
        protected final LVar value;
        protected double curTime = 0;

        public WaitI(LStatement statement) {
            super(statement);
            value = assembler.var(statement.arg0());
        }

        @Override
        public void run() {
            if (value.num() <= 0) {
                yield = true;
                curTime = 0;
            } else if (curTime >= value.num()) {
                if (errorHandler.getFlag(TRACE_EXECUTION)) {
                    errorHandler.info("    Wait finished (requested time %g, actual time %g)", value.num(), curTime);
                }
                waitTime += curTime;
                curTime = 0;
            } else {
                if (errorHandler.getFlag(TRACE_EXECUTION)) {
                    errorHandler.info("    Waiting (requested time %g, actual time %g)", value.num(), curTime);
                }

                //skip back to self.
                counter.numval--;
                yield = true;
                curTime += delta / 60f;
            }

            if (value.num() > 3600) {
                finish(STOP_ON_LONG_WAIT);
            }
        }
    }



    protected class SetRateI extends AbstractInstruction {
        protected final LVar amount;

        public SetRateI(LStatement statement) {
            super(statement);
            amount = assembler.var(statement.arg0());
        }

        @Override
        public void run() {
            ipt.setnum(Math.min(Math.max(1, amount.numi()), 1000));
        }
    }
}
