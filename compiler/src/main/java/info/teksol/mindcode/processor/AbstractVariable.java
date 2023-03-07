package info.teksol.mindcode.processor;

import static info.teksol.mindcode.processor.ProcessorFlag.*;

public abstract class AbstractVariable implements Variable {
    private final boolean fixed;
    private final String name;
    private MindustryObject object;
    private boolean isObject;
    
    public AbstractVariable(String name, MindustryObject object) {
        this.fixed = false;
        this.name = name;
        this.object = object;
        this.isObject = true;
    }

    public AbstractVariable(String name) {
        this.fixed = false;
        this.name = name;
        this.object = null;
        this.isObject = false;
    }

    public AbstractVariable(boolean fixed, String name, MindustryObject object) {
        this.fixed = fixed;
        this.name = name;
        this.object = object;
        this.isObject = true;
    }

    public AbstractVariable(boolean fixed, String name) {
        this.fixed = fixed;
        this.name = name;
        this.object = null;
        this.isObject = false;
    }

    protected abstract String valueToString();

    protected void unsetObject() {
        if (fixed) {
            throw new ExecutionException(ERR_ASSIGNMENT_TO_FIXED_VAR, "Cannot assign to fixed variable " + getName());
        }
        object = null;
        isObject = false;
    }

    @Override
    public boolean isObject() {
        return isObject;
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
        if (!isObject || object == null) {
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
        this.isObject = true;
    }

    @Override
    public String toString() {
        return isObject ? String.valueOf(object) : valueToString();
    }
}
