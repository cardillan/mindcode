package info.teksol.mindcode.processor;

import info.teksol.mindcode.ast.AstNode;

/**
 * Mindustry processor variable.
 */
public interface Variable extends MindustryValue, MindustryResult {
    String getName();

    void assign(Variable var);

    MindustryObject getObject();

    MindustryObject getExistingObject();

    void setObject(MindustryObject object);

    int getIntValue();

    void setIntValue(int value);

    AstNode toAstNode();
}
