package info.teksol.mindcode.v3.compiler.callgraph;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.v3.DataType;
import info.teksol.mindcode.v3.MessageConsumer;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionDeclaration;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
class FunctionDefinitions extends AbstractMessageEmitter {
    private final LogicFunction main;
    private final List<LogicFunction> functionList = new ArrayList<>();
    private final Map<String, List<LogicFunction>> functionMap = new HashMap<>();

    public FunctionDefinitions(MessageConsumer messageConsumer) {
        super(messageConsumer);
        main = new LogicFunction(createMain());
        functionList.add(main);
    }

    public LogicFunction addFunctionDeclaration(AstFunctionDeclaration declaration) {
        LogicFunction current = new LogicFunction(declaration);
        functionList.add(current);

        List<LogicFunction> functions = functionMap.computeIfAbsent(declaration.getName(), k -> new ArrayList<>());
        functions.stream().filter(f -> conflicts(current, f)).forEach(f -> reportConflict(current, f));

        functions.add(current);
        return current;
    }

    private boolean conflicts(LogicFunction f1, LogicFunction f2) {
        if (!f1.getParameterCount().overlaps(f2.getParameterCount())) {
            return false;
        }

        int count = Math.min(f1.getStandardParameterCount(), f2.getStandardParameterCount());
        for (int index = 0; index < count; index++) {
            AstFunctionParameter p1 = f1.getDeclaredParameter(index);
            AstFunctionParameter p2 = f2.getDeclaredParameter(index);
            if (!conflicts(p1, p2)) {
                return false;
            }
        }

        // If one function is varagrs and the other is not, it is not a conflict
        return f1.isVarargs() == f2.isVarargs();
    }

    private boolean conflicts(AstFunctionParameter p1, AstFunctionParameter p2) {
        return p1.isInput() && p2.isInput() || p1.isOutput() && p2.isOutput();
    }

    private void reportConflict(LogicFunction current, LogicFunction previous) {
        String defined = previous.getInputPosition().inputFile() == current.getInputPosition().inputFile()
                ? "" :  " defined in " + previous.getInputPosition().inputFile().getPath();
        error(current.getInputPosition(), "Function '%s' conflicts with function '%s'%s.",
                current.format(), previous.format(), defined);
    }

    public LogicFunction getMain() {
        return main;
    }

    public List<LogicFunction> getFunctions() {
        return functionList;
    }

    public List<LogicFunction> getLooseMatches(AstFunctionCall call) {
        return functionMap.getOrDefault(call.getName(), List.of());
    }

    public List<LogicFunction> getExactMatches(AstFunctionCall call) {
        List<LogicFunction> list = functionMap.getOrDefault(call.getName(), List.of())
                .stream()
                .filter(f -> f.exactMatch(call))
                .toList();

        if (list.size() == 2 && list.get(0).isVarargs() != list.get(1).isVarargs()) {
            return List.of(list.get(list.getFirst().isVarargs() ? 1 : 0));
        } else {
            return list;
        }
    }

    /// If there's just one exact match to this function, it is returned. Otherwise, null is returned.
    ///
    /// @param call function call to match
    /// @return unique function exactly matching the given call
    public LogicFunction getExactMatch(AstFunctionCall call) {
        List<LogicFunction> result = getExactMatches(call);
        return result.size() == 1 ? result.getFirst() : null;
    }


    private static AstFunctionDeclaration createMain() {
        return new AstFunctionDeclaration(InputPosition.EMPTY,
                null,
                new AstIdentifier(InputPosition.EMPTY, ""),
                DataType.VOID,
                List.of(),
                List.of(),
                false,
                false);
    }
}

