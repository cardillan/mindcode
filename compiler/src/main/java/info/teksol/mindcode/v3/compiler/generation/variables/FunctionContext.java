package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/// Provides variable tracking within a function context. Separate implementations exist for global context
/// (outside any function), local context (within main body or non-recursive function) and recursive context.
@NullMarked
public interface FunctionContext {
    /// @return function associated with the context, or null if in global context
    @Nullable LogicFunction function();

    /// Registers a new function variable.
    NodeValue registerFunctionVariable(AstIdentifier identifier);

    /// Returns user variables registered within the function
    Map<String, NodeValue> variables();

    List<FunctionArgument> getVarargs();

    /// Called when entering a new AST node. For tracking variables used within a node
    void enterAstNode();

    /// Called when exiting an AST node. Removes variables belonging to that node.
    void exitAstNode();

    ///  Registers a temporary variable valid in given node.
    void registerNodeVariable(LogicVariable variable);

    ///  Registers a temporary variable within the parent node.
    void registerParentNodeVariable(LogicVariable variable);

    /// Encapsulates processing of given expression, by keeping temporary variable(s) created while evaluating
    /// the expression out of current node context. Suitable when the generated temporary variables are known
    /// not to be used outside the context of the expression. A good example is the condition expression of the
    /// if statement: the condition is evaluated and the result is used to choose the branch to execute, but
    /// all this happens before either of the branches are executed and the temporary variable holding the condition
    /// value will not be used again.
    ///
    /// Note: if x = a > b then ... else ... end; print(x) is not a problem, because x is a user variable and
    /// is registered separately.
    ///
    /// @param <T> type of return value
    /// @param expression expression to evaluate
    /// @return value provided by the expression
    <T> T excludeVariablesFromNode(Supplier<T> expression);
}
