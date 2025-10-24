package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.MessageConsumer;
import info.teksol.mc.mindcode.compiler.CallType;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

@NullMarked
public class FunctionDefinitions extends AbstractMessageEmitter {
    private final MindcodeFunction main;
    private final List<MindcodeFunction> functionList = new ArrayList<>();
    private final Map<String, List<MindcodeFunction>> functionMap = new HashMap<>();
    private final Map<AstFunctionDeclaration, MindcodeFunction> declarationMap = new IdentityHashMap<>();

    public FunctionDefinitions(MessageConsumer messageConsumer, AstModule mainModule) {
        super(messageConsumer);
        main = new MindcodeFunction(createMain(), mainModule, true);
        functionList.add(main);
    }

    public MindcodeFunction addFunctionDeclaration(AstFunctionDeclaration declaration, AstModule module, boolean entryPoint) {
        MindcodeFunction current = new MindcodeFunction(declaration, module, entryPoint);
        functionList.add(current);
        declarationMap.put(declaration, current);

        List<MindcodeFunction> functions = functionMap.computeIfAbsent(declaration.getName(), k -> new ArrayList<>());
        functions.stream().filter(f -> conflicts(current, f)).forEach(f -> reportConflict(current, f));

        functions.add(current);
        return current;
    }

    private boolean conflicts(MindcodeFunction f1, MindcodeFunction f2) {
        if (f1.isRemote() && f2.isRemote()) {
            return true;
        }

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
        if (current.isRemote() && previous.isRemote()) {
            error(current.getSourcePosition(), ERR.FUNCTION_CONFLICT_REMOTE, current.format(), previous.format());
        } else {
            String defined = previous.getSourcePosition().inputFile() == current.getSourcePosition().inputFile()
                    ? "" : " defined in " + previous.getSourcePosition().inputFile().getPath();
            error(current.getSourcePosition(), ERR.FUNCTION_CONFLICT, current.format(), previous.format(), defined);
        }
    }

    public MindcodeFunction getMain() {
        return main;
    }

    public MindcodeFunction findFunction(AstFunctionDeclaration declaration) {
        return Objects.requireNonNull(declarationMap.get(declaration));
    }

    public List<MindcodeFunction> getFunctions() {
        return functionList;
    }

    public List<MindcodeFunction> getFunctions(String name) {
        return functionMap.getOrDefault(name, List.of());
    }

    private Predicate<MindcodeFunction> createRemoteFunctionFilter(AstFunctionCall call) {
        if (call.getObject() == null) {
            return f -> f.getModule().getRemoteProcessors().isEmpty();
        } else if (call.getObject() instanceof AstIdentifier target && !target.isExternal()) {
            return f -> f.getModule().matchesProcessor(target.getName());
        } else {
            return _ -> false;
        }
    }

    public List<MindcodeFunction> getLooseMatches(AstFunctionCall call) {
        return functionMap.getOrDefault(call.getFunctionName(), List.of())
                .stream().filter(createRemoteFunctionFilter(call)).toList();
    }

    public List<MindcodeFunction> getExactMatches(AstFunctionCall call, int callArgumentCount) {
        List<MindcodeFunction> list = functionMap.getOrDefault(call.getFunctionName(), List.of())
                .stream()
                .filter(createRemoteFunctionFilter(call))
                .filter(f -> f.exactMatch(call, callArgumentCount))
                .toList();

        if (list.size() == 2 && list.get(0).isVarargs() != list.get(1).isVarargs()) {
            // When vararg and non-vararg is matched, return only the non-vararg version
            return List.of(list.get(list.getFirst().isVarargs() ? 1 : 0));
        } else {
            return list;
        }
    }

    /// Returns all functions matching the call.
    ///
    /// @param call function call to match
    /// @return unique function exactly matching the given call
    public @Nullable MindcodeFunction getExactMatch(AstFunctionCall call, int callArgumentCount) {
        List<MindcodeFunction> result = getExactMatches(call, callArgumentCount);
        return result.size() == 1 ? result.getFirst() : null;
    }

    private static AstFunctionDeclaration createMain() {
        return new AstFunctionDeclaration(SourcePosition.EMPTY,
                null,
                new AstIdentifier(SourcePosition.EMPTY, ""),
                DataType.VOID,
                List.of(),
                List.of(),
                CallType.NONE);
    }
}

