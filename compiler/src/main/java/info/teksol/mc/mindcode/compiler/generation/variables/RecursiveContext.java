package info.teksol.mc.mindcode.compiler.generation.variables;

import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;

import java.util.*;
import java.util.function.Supplier;

@NullMarked
public class RecursiveContext extends LocalContext {
    private List<LogicVariable> nodeVariables = new ArrayList<>();
    private List<LogicVariable> parentVariables = new ArrayList<>();
    private final Deque<List<LogicVariable>> nodeStack = new ArrayDeque<>();

    public RecursiveContext(MessageConsumer messageConsumer, NameCreator nameCreator, MindcodeFunction function,
            List<FunctionArgument> varargs) {
        super(messageConsumer, nameCreator, function, varargs);
    }

    @Override
    public void enterAstNode() {
        super.enterAstNode();
        nodeStack.push(parentVariables);
        parentVariables = nodeVariables;
        nodeVariables = new ArrayList<>(parentVariables);
    }

    @Override
    public void exitAstNode() {
        nodeVariables = parentVariables;
        parentVariables = nodeStack.pop();
        super.exitAstNode();
    }

    @Override
    public Collection<ValueStore> getActiveVariables() {
        Set<ValueStore> result = new LinkedHashSet<>(super.getActiveVariables());
        result.addAll(nodeVariables);
        return result;
    }

    @Override
    public void registerNodeVariable(LogicVariable variable) {
        nodeVariables.add(variable);
    }

    @Override
    public void registerParentNodeVariable(LogicVariable variable) {
        parentVariables.add(variable);
    }

    @Override
    public <T> T excludeVariablesFromNode(Supplier<T> expression) {
        int savePoint = nodeVariables.size();
        T result = expression.get();
        nodeVariables.subList(savePoint, nodeVariables.size()).clear();
        return result;
    }
}
