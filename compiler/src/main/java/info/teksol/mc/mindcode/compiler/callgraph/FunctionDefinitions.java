package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.common.InputPosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionCall;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionDeclaration;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionParameter;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class FunctionDefinitions extends AbstractMessageEmitter {
    private final MindcodeFunction main;
    private final List<MindcodeFunction> functionList = new ArrayList<>();
    private final Map<String, List<MindcodeFunction>> functionMap = new HashMap<>();

    public FunctionDefinitions(MessageConsumer messageConsumer) {
        super(messageConsumer);
        main = new MindcodeFunction(createMain());
        functionList.add(main);
    }

    public MindcodeFunction addFunctionDeclaration(AstFunctionDeclaration declaration) {
        MindcodeFunction current = new MindcodeFunction(declaration);
        functionList.add(current);

        List<MindcodeFunction> functions = functionMap.computeIfAbsent(declaration.getName(), k -> new ArrayList<>());
        functions.stream().filter(f -> conflicts(current, f)).forEach(f -> reportConflict(current, f));

        functions.add(current);
        return current;
    }

    private boolean conflicts(MindcodeFunction f1, MindcodeFunction f2) {
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

    private void reportConflict(MindcodeFunction current, MindcodeFunction previous) {
        String defined = previous.getInputPosition().inputFile() == current.getInputPosition().inputFile()
                ? "" :  " defined in " + previous.getInputPosition().inputFile().getPath();
        error(current.getInputPosition(), "Function '%s' conflicts with function '%s'%s.",
                current.format(), previous.format(), defined);
    }

    public MindcodeFunction getMain() {
        return main;
    }

    public List<MindcodeFunction> getFunctions() {
        return functionList;
    }

    public List<MindcodeFunction> getLooseMatches(AstFunctionCall call) {
        return functionMap.getOrDefault(call.getFunctionName(), List.of());
    }

    public List<MindcodeFunction> getExactMatches(AstFunctionCall call) {
        List<MindcodeFunction> list = functionMap.getOrDefault(call.getFunctionName(), List.of())
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
    public @Nullable MindcodeFunction getExactMatch(AstFunctionCall call) {
        List<MindcodeFunction> result = getExactMatches(call);
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
