package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.Tuple2;
import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.NoOp;

import java.util.*;
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
        functions.stream().filter(f -> f.getParameterCount().overlaps(current.getParameterCount()))
                        .forEach(f -> reportClash(current, f));

        functions.add(current);
        return current;
    }

    private void reportClash(LogicFunction current, LogicFunction previous) {
        String defined = previous.getInputPosition().inputFile() == current.getInputPosition().inputFile()
                ? "" :  " defined in " + previous.getInputPosition().inputFile().fileName();
        error(current.getInputPosition(), "Function '%s' clashes with function '%s'%s.",
                current.format(), previous.format(), defined);
    }

    public LogicFunction getMain() {
        return main;
    }

    public List<LogicFunction> getFunctions() {
        return functionList;
    }

    /**
     * Finds the closes matching function to given function call. The closest matching function is the one
     * giving the best match score. If a function matching the call perfectly exists, it will be chosen.
     *
     * @param call function call to match
     * @return function matching the given call best
     */
    public Optional<LogicFunction> getFunction(FunctionCall call) {
        return functionMap.getOrDefault(call.getFunctionName(), List.of())
                .stream()
                .map(f -> Tuple2.of(f, f.matchScore(call)))
                .max(Comparator.comparingInt(Tuple2::e2))
                .map(Tuple2::e1);
    }

    private static FunctionDeclaration createMain() {
        return new FunctionDeclaration(null, true, false,
                true, "", List.of(), new NoOp());
    }
}

