package info.teksol.mc.emulator;

import info.teksol.mc.emulator.processor.ExecutionException;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.evaluator.ExpressionEvaluator;
import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.evaluator.LogicWritable;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContents;

import java.util.Objects;

import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_ASSIGNMENT_TO_FIXED_VAR;
import static info.teksol.mc.emulator.processor.ExecutionFlag.ERR_NOT_AN_OBJECT;

public class MindustryVariable implements LogicWritable, LogicReadable {
    private final String name;
    private final boolean mlogConstant;
    private final boolean counter;

    // Actual value of the variable. Variables are created as null objects.
    private boolean isObject;
    private MindustryObject object;
    private double numericValue;

    private MindustryVariable(String name, boolean mlogConstant, boolean counter, boolean isObject,
            MindustryObject object, double numericValue) {
        this.name = Objects.requireNonNull(name);
        this.mlogConstant = mlogConstant;
        this.counter = counter;
        this.isObject = isObject;
        this.object = object;
        this.numericValue = numericValue;
    }

    public static MindustryVariable createCounter() {
        return new MindustryVariable("@counter", false, true, false, null, 0.0);
    }

    public static MindustryVariable createNull() {
        return new MindustryVariable("null", true, false, true, null, 0.0);
    }

    public static MindustryVariable createConstString(String name) {
        return new MindustryVariable(name, true, false, true, new MindustryString(name), 0.0);
    }

    public static MindustryVariable createConstObject(MindustryObject object) {
        return new MindustryVariable(object.name(), true, false, true, object,0.0);
    }

    public static MindustryVariable createUnregisteredContent(String contentName) {
        return new MindustryVariable(contentName, true, false, true, MindustryContents.unregistered(contentName),0.0);
    }

    public static MindustryVariable createConst(String name, boolean value) {
        return new MindustryVariable(name, true, false, false, null, value ? 1.0 : 0.0);
    }

    public static MindustryVariable createConst(String name, int value) {
        return new MindustryVariable(name, true, false, false, null, value);
    }

    public static MindustryVariable createConst(String name, long value) {
        return new MindustryVariable(name, true, false, false, null, value);
    }

    public static MindustryVariable createConst(String name, double value) {
        return new MindustryVariable(name, true, false, false, null, value);
    }

    public static MindustryVariable createVar(String name) {
        return new MindustryVariable(name, false, false, true, null,0.0);
    }

    public String getName() {
        return name;
    }

    public boolean isMlogConstant() {
        return mlogConstant;
    }

    @Override
    public boolean isConstant() {
        // Won't be called in contexts where MindustryVariable is used
        // Kept for consistency.
        return true;
    }

    public boolean isObject() {
        return isObject;
    }

    public void assign(MindustryVariable variable) {
        verifyModification();

        if (variable.isObject()) {
            setObject(variable.getObject());
        } else {
            isObject = false;
            numericValue = ExpressionEvaluator.invalid(variable.numericValue) ? 0.0 : variable.numericValue;
        }
    }

    public void setObject(MindustryObject object) {
        verifyModification();
        if (counter) {
            throw new ExecutionException(ExecutionFlag.ERR_INVALID_COUNTER, "Trying to assign an object to '%s'.", name);
        }
        if (object == null) {
            setNull();
        } else {
            this.isObject = true;
            this.object = object;
        }
    }

    public void setNull() {
        verifyModification();
        if (counter) {
            throw new ExecutionException(ExecutionFlag.ERR_INVALID_COUNTER, "Trying to assign an object to '%s'.", name);
        }
        this.isObject = true;
        this.object = null;
    }

    public void setDoubleValue(double value) {
        verifyModification();
        if (ExpressionEvaluator.invalid(value)) {
            this.isObject = true;
        } else {
            this.isObject = false;
            this.numericValue = value;
        }
        this.object = null;
    }

    public void setLongValue(long value) {
        verifyModification();
        this.isObject = false;
        this.numericValue = value;
        this.object = null;
    }

    public void setIntValue(int value) {
        verifyModification();
        this.isObject = false;
        this.numericValue = value;
        this.object = null;
    }

    public void setBooleanValue(boolean value) {
        verifyModification();
        this.isObject = false;
        this.numericValue = value ? 1.0 : 0.0;
        this.object = null;
    }

    private void verifyModification() {
        if (mlogConstant) {
            throw new ExecutionException(ERR_ASSIGNMENT_TO_FIXED_VAR, "Cannot modify a value of '%s'.", name);
        }
    }

    public MindustryObject getObject() {
        return isObject ? object : null;
    }

    public MindustryObject getExistingObject() {
        if (!isObject || object == null) {
            throw new ExecutionException(ERR_NOT_AN_OBJECT, "Variable '%s' is not an object.", name);
        }
        return object;
    }

    public double getDoubleValue() {
        return isObject ? object != null ? 1 : 0 : ExpressionEvaluator.invalid(numericValue) ? 0 : numericValue;
    }

    public String getStringValue() {
        if (isObject && object instanceof MindustryString str) {
            return str.format();
        }
        throw new UnsupportedOperationException();
    }

    public long getLongValue() {
        return (long) getDoubleValue();
    }

    public int getIntValue() {
        return (int) getDoubleValue();
    }

    public boolean getBooleanValue() {
        return isObject ? object != null : Math.abs(numericValue) >= 0.00001;
    }

    public String print(InstructionProcessor instructionProcessor) {
        return isObject ? print(object) : instructionProcessor.formatNumber(numericValue);
    }

    public String printExact() {
        return isObject ? print(object) : String.valueOf(numericValue);
    }

    public boolean invalidNumber() {
        return isObject || ExpressionEvaluator.invalid(numericValue);
    }

    public static String print(MindustryObject object) {
        return object == null ? "null" : object.format();
    }
}
