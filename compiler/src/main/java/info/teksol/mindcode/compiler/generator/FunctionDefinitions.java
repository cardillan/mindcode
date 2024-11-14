package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.FunctionParameter;
import info.teksol.mindcode.ast.NoOp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class FunctionDefinitions extends AbstractMessageEmitter {
    private final LogicFunction main;
    private final List<LogicFunction> functionList = new ArrayList<>();
    private final Map<String, List<LogicFunction>> functionMap = new HashMap<>();

    public FunctionDefinitions(Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        main = new LogicFunction(createMain());
        functionList.add(main);
    }

    public LogicFunction addFunctionDeclaration(FunctionDeclaration declaration) {
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
            FunctionParameter p1 = f1.getDeclaredParameter(index);
            FunctionParameter p2 = f2.getDeclaredParameter(index);
            if (!conflicts(p1, p2)) {
                return false;
            }
        }

        // If one function is varagrs and the other is not, it is not a conflict
        return f1.isVarArgs() == f2.isVarArgs();
    }

    private boolean conflicts(FunctionParameter p1, FunctionParameter p2) {
        return p1.isInput() && p2.isInput() || p1.isOutput() && p2.isOutput();
    }

    private void reportConflict(LogicFunction current, LogicFunction previous) {
        String defined = previous.getInputPosition().inputFile() == current.getInputPosition().inputFile()
                ? "" :  " defined in " + previous.getInputPosition().inputFile().fileName();
        error(current.getInputPosition(), "Function '%s' conflicts with function '%s'%s.",
                current.format(), previous.format(), defined);
    }

    public LogicFunction getMain() {
        return main;
    }

    public List<LogicFunction> getFunctions() {
        return functionList;
    }

    public List<LogicFunction> getLooseMatches(FunctionCall call) {
        return functionMap.getOrDefault(call.getFunctionName(), List.of());
    }

    public List<LogicFunction> getExactMatches(FunctionCall call) {
        List<LogicFunction> list = functionMap.getOrDefault(call.getFunctionName(), List.of())
                .stream()
                .filter(f -> f.exactMatch(call))
                .toList();

        if (list.size() == 2 && list.get(0).isVarArgs() != list.get(1).isVarArgs()) {
            return List.of(list.get(list.get(0).isVarArgs() ? 1 : 0));
        } else {
            return list;
        }
    }

    /**
     * If there's just one exact match to this function, it is returned. Otherwise, null is returned.
     *
     * @param call function call to match
     * @return unique function exactly matching the given call
     */
    public LogicFunction getExactMatch(FunctionCall call) {
        List<LogicFunction> result = getExactMatches(call);
        return result.size() == 1 ? result.get(0) : null;
    }


    private static FunctionDeclaration createMain() {
        return new FunctionDeclaration(null, null, true, false,
                true, "", List.of(), new NoOp());
    }
}

