package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@NullMarked
public class LocalContext implements FunctionContext {
    private final LogicFunction function;
    private final String functionPrefix;
    private final List<FunctionArgument> varargs;
    private final Map<String, NodeValue> variables = new HashMap<>();

    public LocalContext(LogicFunction function, String functionPrefix, List<FunctionArgument> varargs) {
        this.function = Objects.requireNonNull(function);
        this.functionPrefix = Objects.requireNonNull(functionPrefix);
        this.varargs = Objects.requireNonNull(varargs);
        function.getParameters().forEach(p -> variables.put(p.getName(), p));
    }

    @Override
    public Map<String, NodeValue> variables() {
        return variables;
    }

    @Override
    public List<FunctionArgument> getVarargs() {
        return varargs;
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
    public <T> T excludeVariablesFromNode(Supplier<T> expression) {
        return expression.get();
    }
}