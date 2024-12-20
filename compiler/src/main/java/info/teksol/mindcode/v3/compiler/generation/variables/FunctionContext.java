package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/// Provides variable tracking within a function context. Separate implementations exist for global context
/// (outside any function), local context (within main body or non-recursive function) and recursive context.
@NullMarked
public interface FunctionContext {
    /// @return function associated with the context, or null if in global context
    @Nullable LogicFunction function();

    /// Called when entering a new AST node. For tracking variables used within a node
    void enterAstNode();

    /// Called when exiting an AST node. Removes variables belonging to that node.
    void exitAstNode();

    ///  Registers a temporary variable valid in given node.
    void registerNodeVariable(LogicVariable variable);

    ///  Registers a temporary variable within the parent node.
    void registerParentNodeVariable(LogicVariable variable);

    /// Returns user variables registered within the function
    Map<String, NodeValue> variables();

    /// Registers a new function variable.
    NodeValue registerFunctionVariable(AstIdentifier identifier);
}
