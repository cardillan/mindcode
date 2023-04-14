package info.teksol.mindcode.processor;

import info.teksol.mindcode.ast.AstNode;

import java.util.Optional;

/**
 * Mindustry processor variable.
 */
public interface Variable {
    String getName();
    ValueType getType();
    boolean isObject();

    void assign(Variable var);

    MindustryObject getObject();
    MindustryObject getExistingObject();
    void setObject(MindustryObject object);

    double getDoubleValue();
    void setDoubleValue(double value);

    long getLongValue();
    void setLongValue(long value);

    int getIntValue();
    void setIntValue(int value);

    void setBooleanValue(boolean value);

    Optional<AstNode> toAstNode();
}
