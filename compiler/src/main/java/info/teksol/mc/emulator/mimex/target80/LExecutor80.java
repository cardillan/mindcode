package info.teksol.mc.emulator.mimex.target80;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.MindustryObject;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.mimex.LAssembler;
import info.teksol.mc.emulator.mimex.LStatement;
import info.teksol.mc.emulator.mimex.MimexEmulator;
import info.teksol.mc.emulator.mimex.target60.LExecutor60;
import info.teksol.mc.emulator.mimex.target70.LExecutor70;
import info.teksol.mc.mindcode.logic.mimex.LAccess;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static info.teksol.mc.emulator.ExecutionFlag.*;

@NullMarked
public class LExecutor80 extends LExecutor70 {

    public LExecutor80(MindustryMetadata metadata, LAssembler assembler, MimexEmulator emulator) {
        super(metadata, assembler, emulator);

        builders.put("format", FormatI::new);
        builders.put("printchar", PrintCharI::new);
        builders.put("sensor", SensorI::new);

        readHandlers.add(this::processorRead);
        readHandlers.add(this::charSequenceRead);
        writeHandlers.add(this::processorWrite);
    }

    protected void copyRemoteVar(@Nullable LVar from, LVar to) {
        to.updateStep = step;
        if (from != null && !to.constant) {
            to.objval = from.objval;
            to.numval = from.numval;
            to.isobj = from.isobj;
        }
    }
    protected boolean processorRead(LVar output, Object object, LVar address) {
        if (!(object instanceof LogicBlock block)) {
            return false;
        }

        if (address.obj() instanceof String str) {
            LVar fromVar = block.getOptionalVar(str);
            if (fromVar == null) {
                error(ERR_NONEXISTENT_VAR, "Variable '%s' does not exist in block '%s'", str, block);
            }
            if (checkWritable(output)) {
                copyRemoteVar(fromVar, output);
            }
        } else {
            error(ERR_NONEXISTENT_VAR, "Variable name not a string: %s", address);
        }
        return true;
    }

    protected boolean charSequenceRead(LVar output, Object object, LVar address) {
        if (!(object instanceof CharSequence str)) {
            return false;
        }

        if (address.invalidNumber()) {
            error(ERR_NOT_A_NUMBER, "Invalid numeric value in read address '%s'.", address);
        }

        // Reading outside string is not an error
        int index = address.numi();
        setVar(output, index < 0 || index >= str.length() ? Double.NaN : (int) str.charAt(index));
        return true;
    }

    protected boolean processorWrite(LVar input, Object object, LVar address) {
        if (!(object instanceof LogicBlock block)) {
            return false;
        }

        if (address.obj() instanceof String str) {
            LVar toVar = block.getOptionalVar(str);
            if (toVar == null) {
                error(ERR_NONEXISTENT_VAR, "Variable '%s' does not exist in block '%s'", str, block);
            } else {
                copyRemoteVar(input, toVar);
            }
        } else {
            error(ERR_NONEXISTENT_VAR, "Variable name not a string: %s", address);
        }
        return true;
    }


    @Override
    protected String formatDouble(double value) {
        if (Math.abs(value - Math.round(value)) < 0.00001) {
            return String.valueOf(Math.round(value));
        } else {
            return String.valueOf(value);
        }
    }

    protected class FormatI extends PrintI {
        public FormatI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            if (!textBuffer.format(formatValue())) {
                error(ERR_INVALID_FORMAT, "No valid formatting placeholder found in the text buffer.");
            }
        }
    }

    protected class PrintCharI extends AbstractInstruction {
        protected final LVar value;

        public PrintCharI(LStatement statement) {
            super(statement);
            value = assembler.var(statement.arg0());
        }

        @Override
        public void run() {
            /// §§§ error reporting
            if (value.isobj) {
                if (value.objval instanceof MindustryObject object) {
                    textBuffer.print(Objects.requireNonNullElse(object.iconString(metadata), ""));
                }
            } else {
                textBuffer.print(String.valueOf((char) Math.floor(value.numval)));
            }
        }
    }

    protected class SensorI extends LExecutor60.SensorI {
        public SensorI(LStatement statement) {
            super(statement);
        }

        @Override
        public void run() {
            if (from.obj() instanceof CharSequence str && type.obj() instanceof LAccess access
                    && (access.name().equals("@size") || access.name().equals("@bufferSize"))) {
                setVar(to, str.length());
            } else {
                super.run();
            }
        }
    }
}
