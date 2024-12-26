package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunctionV3;
import info.teksol.mindcode.v3.compiler.generation.LoopStack;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Supplier;

@NullMarked
public class LocalContext implements FunctionContext {
    private final LogicFunctionV3 function;
    private final List<FunctionArgument> varargs;
    private final Map<String, NodeValue> variables = new HashMap<>();
    private final LoopStack loopStack;

    public LocalContext(MessageConsumer messageConsumer, LogicFunctionV3 function, List<FunctionArgument> varargs) {
        this.function = Objects.requireNonNull(function);
        this.varargs = Objects.requireNonNull(varargs);
        this.loopStack = new LoopStack(messageConsumer);
        function.getParameters().forEach(p -> variables.put(p.getName(), p));
    }

    public LoopStack loopStack() {
        return loopStack;
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
    public Collection<NodeValue> getActiveVariables() {
        return variables.values();
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
        if (function.isMain()) {
            return LogicVariable.main(name);
        } else {
            return LogicVariable.local(function.getName(), function.getPrefix(), name);
        }
    }

    @Override
    public LogicFunctionV3 function() {
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
