package info.teksol.emulator;

import info.teksol.emulator.processor.ExecutionException;
import info.teksol.emulator.processor.ExecutionFlag;
import info.teksol.evaluator.LogicReadable;
import info.teksol.evaluator.LogicWritable;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.mimex.MindustryContents;

import java.util.Objects;

import static info.teksol.emulator.MindustryVariable.ValueType.*;
import static info.teksol.emulator.processor.ExecutionFlag.ERR_ASSIGNMENT_TO_FIXED_VAR;
import static info.teksol.emulator.processor.ExecutionFlag.ERR_NOT_AN_OBJECT;

public class MindustryVariable implements LogicWritable, LogicReadable {
    // TODO Use different implementation for the compiler and remove the type from this class
    enum ValueType { NULL, BOOLEAN, LONG, DOUBLE, OBJECT }

    private final String name;
    private final boolean constant;
    private final boolean counter;

    // Actual value of the variable. Variables are created as null objects.
    private ValueType valueType;
    private boolean isObject;
    private MindustryObject object;
    private double numericValue;

    private MindustryVariable(String name, boolean constant, boolean counter, ValueType valueType, boolean isObject,
            MindustryObject object, double numericValue) {
        this.name = Objects.requireNonNull(name);
        this.constant = constant;
        this.counter = counter;
        this.valueType = Objects.requireNonNull(valueType);
        this.isObject = isObject;
        this.object = object;
        this.numericValue = numericValue;
    }

    public static MindustryVariable createCounter() {
        return new MindustryVariable("@counter", false, true, LONG, false, null, 0.0);
    }

    public static MindustryVariable createNull() {
        return new MindustryVariable("null", true, false, NULL, true, null, 0.0);
    }

    public static MindustryVariable createConstString(String name) {
        return new MindustryVariable(name, true, false, OBJECT, true, new MindustryString(name), 0.0);
    }

    public static MindustryVariable createConstObject(MindustryObject object) {
        return new MindustryVariable(object.name(), true, false, OBJECT, true, object,0.0);
    }

    public static MindustryVariable createUnregisteredContent(String contentName) {
        return new MindustryVariable(contentName, true, false, OBJECT, true, MindustryContents.unregistered(contentName),0.0);
    }

    public static MindustryVariable createConst(String name, boolean value) {
        return new MindustryVariable(name, true, false, BOOLEAN, false, null, value ? 1.0 : 0.0);
    }

    public static MindustryVariable createConst(String name, int value) {
        return new MindustryVariable(name, true, false, LONG, false, null, value);
    }

    public static MindustryVariable createConst(String name, long value) {
        return new MindustryVariable(name, true, false, LONG, false, null, value);
    }

    public static MindustryVariable createConst(String name, double value) {
        return new MindustryVariable(name, true, false, DOUBLE, false, null, value);
    }

    public static MindustryVariable createVar(String name) {
        return new MindustryVariable(name, false, false, OBJECT, true, null,0.0);
    }

    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return constant;
    }

    @Override
    public boolean canEvaluate() {
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
            valueType = variable.valueType;
            isObject = false;
            numericValue = invalid(variable.numericValue) ? 0.0 : variable.numericValue;
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
            this.valueType = OBJECT;
            this.isObject = true;
            this.object = object;
        }
    }

    public void setNull() {
        verifyModification();
        if (counter) {
            throw new ExecutionException(ExecutionFlag.ERR_INVALID_COUNTER, "Trying to assign an object to '%s'.", name);
        }
        this.valueType = NULL;
        this.isObject = true;
        this.object = null;
    }

    public void setDoubleValue(double value) {
        verifyModification();
        if (invalid(value)) {
            this.valueType = NULL;
            this.isObject = true;
        } else {
            this.valueType = DOUBLE;
            this.isObject = false;
            this.numericValue = value;
        }
        this.object = null;
    }

    public void setLongValue(long value) {
        verifyModification();
        this.valueType = LONG;
        this.isObject = false;
        this.numericValue = value;
        this.object = null;
    }

    public void setIntValue(int value) {
        verifyModification();
        this.valueType = LONG;
        this.isObject = false;
        this.numericValue = value;
        this.object = null;
    }

    public void setBooleanValue(boolean value) {
        verifyModification();
        this.valueType = BOOLEAN;
        this.isObject = false;
        this.numericValue = value ? 1.0 : 0.0;
        this.object = null;
    }

    private void verifyModification() {
        if (constant) {
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
        return isObject ? object != null ? 1 : 0 : invalid(numericValue) ? 0 : numericValue;
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

    public String print() {
        return isObject ? print(object) : print(numericValue);
    }

    public String printExact() {
        return isObject ? print(object) : String.valueOf(numericValue);
    }

    public boolean invalidNumber() {
        return isObject || invalid(numericValue);
    }

    // TODO: compiler/optimizer will need to use this eventually
    public static boolean invalid(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    public static String print(MindustryObject object) {
        return object == null ? "null" : object.format();
    }

    public static String print(double value) {
        if(Math.abs(value - (long) value) < 0.00001) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value);
        }
    }

    // TODO track original token/source file for constants
    public AstNode toAstNode() {
        return switch (valueType) {
            case NULL    -> new NullLiteral( null);
            case BOOLEAN -> new BooleanLiteral( null, getIntValue() != 0);
            case LONG    -> new NumericLiteral( null, String.valueOf(getLongValue()));
            case DOUBLE  -> invalid(numericValue)
                    ? new NullLiteral( null)
                    : new NumericValue( null, numericValue);
            case OBJECT  -> new StringLiteral( null, print(object));
        };
    }
}
