package info.teksol.mc.mindcode.decompiler;

import info.teksol.mc.mindcode.logic.arguments.ArgumentType;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.ValueMutability;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Represents a decompiled expression. The expression has at least one input and exactly one output.
/// The simplest expression encapsulates just one variable.
///
/// Any input variable in the decompiled expression can get replaced by another decompiled expression,
/// creating an expression tree.
@NullMarked
public interface MlogExpression extends LogicArgument {

    /// Adds all input variables within this expression into the list
    void gatherInputVariables(List<MlogVariable> variables);

    /// @return the number of leaf nodes (variables) in this expression tree.
    int size();

    /// Replaces all occurrences of the variable with an expression, in all nodes of the tree. Returns
    /// the number of replacements made.
    ///
    /// @param variable variable to replace
    /// @param expression expression which will replace the variable
    void replaceVariable(MlogVariable variable, MlogExpression expression);

    /**
     * @return the Mindcode representation of the expression
     */
    String toMlog();

    @Override
    default ArgumentType getType() {
        return ArgumentType.UNSPECIFIED;
    }

    @Override
    default ValueMutability getMutability() {
        return ValueMutability.MUTABLE;
    }
}
