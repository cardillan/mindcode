package info.teksol.mindcode.processor;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.BooleanLiteral;
import info.teksol.mindcode.ast.NullLiteral;
import info.teksol.mindcode.ast.NumericLiteral;
import info.teksol.mindcode.ast.StringLiteral;
import info.teksol.mindcode.mindustry.generator.GenerationException;

import static info.teksol.mindcode.processor.ProcessorFlag.*;

public abstract class AbstractVariable implements Variable {
    private final boolean fixed;
    private final String name;
    private ValueType type;
    private MindustryObject object;
    
    public AbstractVariable(String name, MindustryObject object) {
        this.fixed = false;
        this.name = name;
        this.object = object;
        this.type = ValueType.OBJECT;
    }

    public AbstractVariable(String name) {
        this.fixed = false;
        this.name = name;
        this.object = null;
        this.type = ValueType.NULL;
    }

    public AbstractVariable(boolean fixed, String name, MindustryObject object) {
        this.fixed = fixed;
        this.name = name;
        this.object = object;
        this.type = ValueType.OBJECT;
    }

    public AbstractVariable(boolean fixed, String name) {
        this.fixed = fixed;
        this.name = name;
        this.object = null;
        this.type = ValueType.NULL;
    }

    protected abstract String valueToString();

    @Override
    public ValueType getType() {
        return type;
    }

    protected void setType(ValueType type) {
        if (fixed) {
            throw new ExecutionException(ERR_ASSIGNMENT_TO_FIXED_VAR, "Cannot assign to fixed variable " + getName());
        }
        this.type = type;
        if (type != ValueType.OBJECT) {
            object = null;
        }
    }

    @Override
    public boolean isObject() {
        return type == ValueType.OBJECT;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MindustryObject getObject() {
        return object;
    }

    @Override
    public MindustryObject getExistingObject() {
        if (!isObject() || object == null) {
            throw new ExecutionException(ERR_NOT_AN_OBJECT, "Variable " + getName() + " is not an object");
        }
        return object;
    }

    @Override
    public void setObject(MindustryObject object) {
        if (fixed) {
            throw new ExecutionException(ERR_ASSIGNMENT_TO_FIXED_VAR, "Cannot assign to fixed variable " + getName());
        }
        this.object = object;
        this.type = object == null ? ValueType.NULL : ValueType.OBJECT;
    }

    @Override
    public void setBooleanValue(boolean value) {
        setIntValue(value ? 1 : 0);
        setType(ValueType.BOOLEAN);
    }

    @Override
    public String toString() {
        return isObject() ? String.valueOf(object) : valueToString();
    }

    @Override
    public AstNode toAstNode() {
        switch (getType()) {
            case NULL:      return new NullLiteral();
            case BOOLEAN:   return new BooleanLiteral(getIntValue() != 0);
            case LONG:      return new NumericLiteral(String.valueOf(getLongValue()));
            case DOUBLE:    return new NumericLiteral(valueToString());
            case OBJECT:    return new StringLiteral(String.valueOf(object));
            default:        throw new GenerationException("Unhandled value type " + getType());
        }
    }
}
