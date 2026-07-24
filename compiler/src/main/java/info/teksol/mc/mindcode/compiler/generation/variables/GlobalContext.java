package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    public boolean isLocal() {
        return false;
    }

    @Override
    public @Nullable MindcodeFunction function() {
        return null;
    }

    @Override
    public @Nullable SourcePosition testUnresolvedGlobal(String name) {
        return null;
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
    public void gatherActiveVariables(Collection<ValueStore> variables) {
        // Do nothing
    }

    @Override
    public void replaceFunctionVariable(AstIdentifier identifier, ValueStore variable) {
        throw new MindcodeInternalError("Trying to replace a local variable in global context:" + identifier);
    }

    @Override
    public ValueStore registerFunctionVariable(AstIdentifier identifier, VariableScope scope, boolean noinit, boolean allowRedefinition) {
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
