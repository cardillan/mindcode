package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@NullMarked
public class LocalContext extends AbstractMessageEmitter implements FunctionContext {
    private final NameCreator nameCreator;
    private final MindcodeFunction function;
    private final List<FunctionArgument> varargs;
    private final Map<String, ValueStore> variables = new LinkedHashMap<>();
    private final LoopStack loopStack;

    /// Keeps track of the number of declarations of the same variable within the function
    /// (in non-overlapping nodes)
    private final Map<String, AtomicInteger> variableReuses = new HashMap<>();

    /// List of active variables allocated within the function, in order of allocation.
    private final List<String> nodeVariables = new ArrayList<>();

    /// Start of the current node in the nodeVariables list
    private int currentNodeIndex = 0;

    /// Keeps starting positions inside the nodeVariables list for all active (nested) nodes.
    private final Deque<Integer> nodeStack = new ArrayDeque<>(50);

    public LocalContext(MessageConsumer messageConsumer, NameCreator nameCreator, MindcodeFunction function,
            List<FunctionArgument> varargs) {
        super(messageConsumer);
        this.nameCreator = nameCreator;
        this.function = Objects.requireNonNull(function);
        this.varargs = Objects.requireNonNull(varargs);
        this.loopStack = new LoopStack(messageConsumer);
        function.getParameters().forEach(p -> putVariable(p.getName(), p));
    }

    private @Nullable ValueStore putVariable(String name, ValueStore variable) {
        return variables.put(name, variable);
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

    public void replaceFunctionVariable(AstIdentifier identifier, ValueStore variable) {
        putVariable(identifier.getName(), variable);
    }

    @Override
    public ValueStore registerFunctionVariable(AstIdentifier identifier, VariableScope scope, boolean noinit, boolean implicitDeclaration) {
        ValueStore variable = createFunctionVariable(identifier, noinit, implicitDeclaration);
        ValueStore existing = putVariable(identifier.getName(), variable);
        if (existing != null) {
            throw new MindcodeInternalError("Repeated registration of function variable (existing variable: %s, AST identifier: %s).",
                    existing, identifier);
        }

        switch (scope) {
            case NODE -> nodeVariables.add(identifier.getName());
            case PARENT_NODE -> {
                if (currentNodeIndex != nodeVariables.size()) {
                    throw new MindcodeInternalError("Adding to parent node while child node variables exist");
                }
                nodeVariables.add(identifier.getName());
                currentNodeIndex++;
            }
        }

        return variable;
    }

    private String getMlogName(ValueStore valueStore) {
        if (valueStore instanceof LogicVariable var) {
            return var.toMlog();
        }
        throw new MindcodeInternalError("Unsupported local variable type");
    }

    private ValueStore createFunctionVariable(AstIdentifier identifier, boolean noinit, boolean implicitDeclaration) {
        int index = variableReuses.computeIfAbsent(identifier.getName(), k -> new AtomicInteger(0)).getAndIncrement();
        if (implicitDeclaration && index > 0) {
            error(identifier, ERR.VARIABLE_NOT_RESOLVED, identifier.getName());
        }

        if (function.isMain()) {
            return LogicVariable.main(identifier, nameCreator.main(identifier.getName(), index), noinit);
        } else {
            return LogicVariable.local(identifier, function, nameCreator.local(function, identifier.getName(), index), noinit);
        }
    }

    @Override
    public MindcodeFunction function() {
        return function;
    }

    @Override
    public void enterAstNode() {
        nodeStack.push(currentNodeIndex);
        currentNodeIndex = nodeVariables.size();
    }

    @Override
    public void exitAstNode() {
        List<String> variablesToRemove = nodeVariables.subList(currentNodeIndex, nodeVariables.size());
        variablesToRemove.forEach(variables::remove);
        variablesToRemove.clear();
        currentNodeIndex = nodeStack.pop();
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
