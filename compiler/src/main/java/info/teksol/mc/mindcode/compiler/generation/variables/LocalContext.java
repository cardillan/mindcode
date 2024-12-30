package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Supplier;

@NullMarked
public class LocalContext implements FunctionContext {
    private final MindcodeFunction function;
    private final List<FunctionArgument> varargs;
    private final Map<String, ValueStore> variables = new LinkedHashMap<>();
    private final LoopStack loopStack;

    public LocalContext(MessageConsumer messageConsumer, MindcodeFunction function, List<FunctionArgument> varargs) {
        this.function = Objects.requireNonNull(function);
        this.varargs = Objects.requireNonNull(varargs);
        this.loopStack = new LoopStack(messageConsumer);
        function.getParameters().forEach(p -> variables.put(p.getName(), p));
    }

    public LoopStack loopStack() {
        return loopStack;
    }

    @Override
    public Map<String, ValueStore> variables() {
        return variables;
    }

    @Override
    public List<FunctionArgument> getVarargs() {
        return varargs;
    }

    @Override
    public Collection<ValueStore> getActiveVariables() {
        return variables.values();
    }

    @Override
    public ValueStore registerFunctionVariable(AstIdentifier identifier) {
        ValueStore variable = createFunctionVariable(identifier.getName());
        ValueStore existing = variables.put(identifier.getName(), variable);
        if (existing != null) {
            throw new MindcodeInternalError("Repeated registration of function variable (existing variable: %s, new variable: %s).",
                    existing, variable);
        }
        return variable;
    }

    private ValueStore createFunctionVariable(String name) {
        if (function.isMain()) {
            return LogicVariable.main(name);
        } else {
            return LogicVariable.local(function.getName(), function.getPrefix(), name);
        }
    }

    @Override
    public MindcodeFunction function() {
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
