package info.teksol.mindcode.v3.compiler.generation.variables;

import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunctionV3;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Supplier;

@NullMarked
public class RecursiveContext extends LocalContext {
    private final List<LogicVariable> nodeVariables = new ArrayList<>();
    private final Deque<Integer> nodeStack = new ArrayDeque<>();
    private int parentIndex = 0;

    public RecursiveContext(MessageConsumer messageConsumer, LogicFunctionV3 function, List<FunctionArgument> varargs) {
        super(messageConsumer, function, varargs);
    }

    @Override
    public void enterAstNode() {
        nodeStack.push(parentIndex);
        parentIndex = nodeVariables.size();
    }

    @Override
    public void exitAstNode() {
        nodeVariables.subList(parentIndex, nodeVariables.size()).clear();
        parentIndex = nodeStack.pop();
    }

    @Override
    public Collection<NodeValue> getActiveVariables() {
        Set<NodeValue> result = new HashSet<>(super.getActiveVariables());
        result.addAll(nodeVariables);
        return result;
    }

    @Override
    public void registerNodeVariable(LogicVariable variable) {
        nodeVariables.add(variable);
    }

    @Override
    public void registerParentNodeVariable(LogicVariable variable) {
        nodeVariables.add(parentIndex++, variable);
    }

    @Override
    public <T> T excludeVariablesFromNode(Supplier<T> expression) {
        // Remember the current size and parent position
        int previousSize = nodeVariables.size();
        int previousParent = parentIndex;
        T result = expression.get();

        // Accommodate variables meanwhile registered in the parent
        int newSize = previousSize + (parentIndex - previousParent);
        nodeVariables.subList(newSize, nodeVariables.size()).clear();
        return result;
    }
}
