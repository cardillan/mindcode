package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.generation.LoopStack;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.util.MutableInteger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NullMarked
public class LocalContext extends CompilerMessageEmitter implements FunctionContext {
    protected final FunctionContext parentContext;
    private final NameCreator nameCreator;
    private final MindcodeFunction function;
    private final List<FunctionArgument> varargs;
    private final Map<String, ValueStore> variables = new LinkedHashMap<>();
    private final LoopStack loopStack;

    /// Keeps the number of times a variable with the same name is declared within the function
    /// (in non-overlapping nodes)
    private final Map<String, MutableInteger> variableReuses = new HashMap<>();

    /// Tracks node variables usage. Ensures variables are removed from the variables map when the node exits.
    private final VariableStack nodeVariables = new VariableStack(variables::remove);

    /// Tracks active loop control variables.
    private final VariableStack loopControlVariables = new VariableStack();

    public LocalContext(MessageConsumer messageConsumer, NameCreator nameCreator, FunctionContext parentContext,
            MindcodeFunction function, List<FunctionArgument> varargs) {
        super(messageConsumer);
        this.nameCreator = nameCreator;
        this.parentContext = parentContext;
        this.function = Objects.requireNonNull(function);
        this.varargs = Objects.requireNonNull(varargs);
        this.loopStack = new LoopStack(messageConsumer);
        function.getParameters().forEach(p -> putVariable(p.getName(), p.isConstant() ? LogicNull.NULL : p));
    }

    private @Nullable ValueStore putVariable(String name, ValueStore variable) {
        return variables.put(name, variable);
    }

    @Override
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
    public void gatherActiveVariables(Collection<ValueStore> variables) {
        parentContext.gatherActiveVariables(variables);
        variables.addAll(this.variables.values());
    }

    @Override
    public void replaceFunctionVariable(AstIdentifier identifier, ValueStore variable) {
        putVariable(identifier.getName(), variable);
    }

    @Override
    public int getVariableReuseCount(AstIdentifier identifier) {
        return variableReuses.computeIfAbsent(identifier.getName(), _ -> MutableInteger.zero()).getAndIncrement();
    }

    @Override
    public ValueStore createFunctionVariable(AstIdentifier identifier, boolean noinit, boolean optional, boolean implicitDeclaration) {
        int index = getVariableReuseCount(identifier);
        if (implicitDeclaration && index > 0) {
            error(identifier, ERR.VARIABLE_NOT_RESOLVED, identifier.getName());
        }

        if (function.isMain()) {
            return LogicVariable.main(identifier, nameCreator.main(identifier.getName(), index), noinit, optional);
        } else {
            return LogicVariable.local(identifier, function, nameCreator.local(function, identifier.getName(), index), noinit, optional);
        }
    }

    @Override
    public ValueStore registerFunctionVariable(AstIdentifier identifier, VariableScope scope, ValueStore variable) {
        ValueStore existing = putVariable(identifier.getName(), variable);
        if (existing != null) {
            throw new MindcodeInternalError("Repeated registration of function variable (existing variable: %s, AST identifier: %s).",
                    existing, identifier);
        }

        switch (scope) {
            case LOOP_CONTROL, NODE -> nodeVariables.add(identifier.getName());
            case PARENT_NODE -> nodeVariables.addToParent(identifier.getName());
        }

        return variable;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public MindcodeFunction function() {
        return function;
    }

    @Override
    public @Nullable SourcePosition testUnresolvedGlobal(String name) {
        return function.testUnresolvedGlobal(name);
    }

    @Override
    public void enterAstNode() {
        nodeVariables.push();
        loopControlVariables.push();
    }

    @Override
    public void exitAstNode() {
        loopControlVariables.pop();
        nodeVariables.pop();
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

    @Override
    public void registerLoopControlVariable(CompilerMessageEmitter emitter, SourceElement element, LogicVariable variable) {
        if (loopControlVariables.exists(variable.toMlog())) {
            emitter.error(element, ERR.LOOP_CONTROL_VARIABLE_REUSED, variable.getFullName());
        } else {
            loopControlVariables.add(variable.toMlog());
        }
    }

    private static class VariableStack {
        /// List of active variables allocated within the function, in order of allocation.
        private final List<String> variables = new ArrayList<>();

        /// Start of the current node in the nodeVariables list
        private int currentIndex = 0;

        /// Keeps starting positions inside the nodeVariables list for all active (nested) nodes.
        private final Deque<Integer> stack = new ArrayDeque<>(50);

        /// A cleanup routine
        private final Consumer<String> cleanup;

        public VariableStack() {
            this.cleanup = _ -> {};
        }

        public VariableStack(Consumer<String> cleanup) {
            this.cleanup = cleanup;
        }

        public void add(String name) {
            variables.add(name);
        }

        public void addToParent(String name) {
            if (!isEmpty()) {
                throw new MindcodeInternalError("Adding to parent node while child node variables exist");
            }
            variables.add(name);
            currentIndex++;
        }

        public boolean exists(String name) {
            return variables.contains(name);
        }

        public void push() {
            stack.push(currentIndex);
            currentIndex = variables.size();
        }

        public void pop() {
            List<String> variablesToRemove = variables.subList(currentIndex, variables.size());
            variablesToRemove.forEach(cleanup);
            variablesToRemove.clear();
            currentIndex = stack.pop();
        }

        public boolean isEmpty() {
            return currentIndex == variables.size();
        }
    }
}
