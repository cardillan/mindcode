package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@NullMarked
public class GlobalContext implements FunctionContext {
    @Override
    public @Nullable LogicFunction function() {
        return null;
    }

    @Override
    public void enterAstNode() {
        // Do nothing
    }

    @Override
    public void exitAstNode() {
        // Do nothing
    }

    @Override
    public void registerNodeVariable(LogicVariable variable) {
        // Do nothing
    }

    @Override
    public void registerParentNodeVariable(LogicVariable variable) {
        // Do nothing
    }

    @Override
    public Map<String, NodeValue> variables() {
        return Map.of();
    }

    @Override
    public NodeValue registerFunctionVariable(AstIdentifier identifier) {
        throw new MindcodeInternalError("Trying to register a local variable in global context:" + identifier);
    }
}
