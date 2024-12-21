package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class LocalContext implements FunctionContext {
    private final LogicFunction function;
    private final String functionPrefix;
    private final Map<String, NodeValue> variables = new HashMap<>();

    public LocalContext(LogicFunction function, String functionPrefix) {
        this.function = Objects.requireNonNull(function);
        this.functionPrefix = Objects.requireNonNull(functionPrefix);
        function.getParameters().forEach(p -> variables.put(p.getName(), p));
    }

    @Override
    public LogicFunction function() {
        return function;
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
        return variables;
    }

    @Override
    public NodeValue registerFunctionVariable(AstIdentifier identifier) {
        NodeValue variable = createFunctionVariable(identifier.getName());
        NodeValue existing = variables.put(identifier.getName(), variable);
        if (existing != null) {
            throw new MindcodeInternalError("Repeated registration of function variable (existing variable: %s, new variable: %s).",
                    existing, variable);
        }
        return variable;
    }

    private NodeValue createFunctionVariable(String name) {
        if (functionPrefix.isEmpty()) {
            return LogicVariable.main(name);
        } else {
            return LogicVariable.local(function.getName(), functionPrefix, name);
        }
    }
}
