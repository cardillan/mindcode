package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunctionV3;
import info.teksol.mindcode.v3.compiler.generation.LoopStack;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@NullMarked
public class GlobalContext implements FunctionContext {
    @Override
    public LoopStack loopStack() {
        throw new MindcodeInternalError("Loop stack not available in global context.");
    }

    @Override
    public LogicFunctionV3 function() {
        throw new MindcodeInternalError("Function not available in global context");
    }

    @Override
    public Map<String, ValueStore> variables() {
        return Map.of();
    }

    @Override
    public List<FunctionArgument> getVarargs() {
        return List.of();
    }

    @Override
    public Collection<ValueStore> getActiveVariables() {
        return List.of();
    }

    @Override
    public ValueStore registerFunctionVariable(AstIdentifier identifier) {
        throw new MindcodeInternalError("Trying to register a local variable in global context:" + identifier);
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
    public <T> T excludeVariablesFromNode(Supplier<T> expression) {
        return expression.get();
    }
}
