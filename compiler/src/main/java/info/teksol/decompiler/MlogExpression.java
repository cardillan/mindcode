package info.teksol.decompiler;

import info.teksol.mindcode.logic.ArgumentType;
import info.teksol.mindcode.logic.LogicArgument;

import java.util.List;

/**
 * Represents a decompiled expression. The expression has at least one input and exactly one output.
 * The simplest expression encapsulates just one variable.
 * <p>
 * Any input variable in the decompiled expression can get replaced by another decompiled expression,
 * creating an expression tree.
 */
public interface MlogExpression extends LogicArgument {

    /**
     * Adds all input variables within this expression into the list
     */
    void gatherInputVariables(List<MlogVariable> variables);

    /**
     * @return the number of leaf nodes (variables) in this expression tree.
     */
    int size();

    /**
     * Replaces all occurrences of the variable with an expression, in all nodes of the tree. Returns
     * the number of replacements made.
     *
     * @param variable variable to replace
     * @param expression expression which will replace the variable
     */
    void replaceVariable(MlogVariable variable, MlogExpression expression);

    /**
     * @return the Mindcode representation of the expression
     */
    String toMlog();

    @Override
    default ArgumentType getType() {
        return ArgumentType.UNSPECIFIED;
    }
}
