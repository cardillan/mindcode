package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
public class CallGraphCreator extends AbstractMessageEmitter {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final AstProgram program;
    private final FunctionDefinitions functionDefinitions;
    private final List<MindcodeFunction> functions = new ArrayList<>();
    private @Nullable AstModule activeModule;
    private MindcodeFunction activeFunction;

    public static CallGraph createCallGraph(CallGraphCreatorContext context, AstProgram program) {
        return new CallGraphCreator(context, program).buildCallGraph();
    }

    private CallGraphCreator(CallGraphCreatorContext context, AstProgram program) {
        super(context.messageConsumer());
        this.profile = context.compilerProfile();
        this.processor = context.instructionProcessor();
        this.program = program;
        this.functionDefinitions = new FunctionDefinitions(messageConsumer);
        this.activeFunction = functionDefinitions.getMain();
    }

    private CallGraph buildCallGraph() {
        // Create functions from the AST tree
        visitNode(program);

        // Setup individual functions
        functions.addAll(functionDefinitions.getFunctions());
        functions.forEach(f -> f.initializeCalls(functionDefinitions));

        buildCallTree();

        functions.forEach(this::validateFunction);

        return new CallGraph(functionDefinitions);
    }

    private void visitNode(AstMindcodeNode nodeToVisit) {
        switch (nodeToVisit) {
            case AstModule m -> {
                activeModule = m;
                nodeToVisit.getChildren().forEach(this::visitNode);
                activeModule = null;
            }
            case AstFunctionDeclaration n   -> visitFunctionDeclaration(n);
            case AstFunctionCall n          -> visitFunctionCall(n);
            default                         -> nodeToVisit.getChildren().forEach(this::visitNode);
        }
    }

    private void visitFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
        Objects.requireNonNull(activeModule, "No active module");

        if (activeModule.getRemoteProcessor() == null) {
            // Only process function bodies in non-remote modules
            MindcodeFunction previousFunction = activeFunction;
            activeFunction = functionDefinitions.addFunctionDeclaration(functionDeclaration,
                    !program.isMainProgram() && functionDeclaration.isRemote() && activeModule == program.getMainModule());
            functionDeclaration.getBody().forEach(this::visitNode);
            activeFunction = previousFunction;
        } else if (functionDeclaration.isRemote()) {
            // Add remote functions in remote modules
            functionDefinitions.addFunctionDeclaration(functionDeclaration, false);
        }
    }

    private void visitFunctionCall(AstFunctionCall functionCall) {
        activeFunction.addCall(functionCall);
        functionCall.getArguments().forEach(this::visitNode);
    }

    /// Inspects the structure of function calls and sets function properties accordingly. Assigns a function prefix
    /// and labels to recursive and stackless functions.
    void buildCallTree() {
        List<MindcodeFunction> entryPoints = functionDefinitions.getFunctions().stream().filter(MindcodeFunction::isEntryPoint).toList();
        buildCallTreeAtRoot(entryPoints, true);

        List<MindcodeFunction> unvisited = functions.stream().filter(f -> !f.isVisited()).toList();
        for (MindcodeFunction function : unvisited) {
            if (!function.isVisited()) {
                buildCallTreeAtRoot(List.of(function), false);
            }
        }

        functions.stream().filter(f -> !f.isMain() && !f.isInline()).forEach(this::setupOutOfLineFunction);
    }

    void buildCallTreeAtRoot(List<MindcodeFunction> entryPoints, boolean trackUsages) {
        // Walk the call tree
        entryPoints.forEach(function -> visitFunction(function, trackUsages, 1));

        // 1st level of indirect calls
        functions.forEach(outer ->
                outer.getCallCardinality().keySet().forEach(inner ->
                        outer.addIndirectCalls(inner.getDirectCalls())
                )
        );

        // 2nd and other levels of indirection
        // Must end eventually, as there's a finite number of functions to add
        while (propagateIndirectCalls()) ;
    }

    private void setupOutOfLineFunction(MindcodeFunction function) {
        function.setLabel(processor.nextLabel());
        function.setPrefix(function.isRemote() ? ":" + function.getName() : processor.nextFunctionPrefix());
        function.createParameters();
    }

    private boolean propagateIndirectCalls() {
        // Returns true if at least one function was modified
        return functions.stream()
                .flatMapToInt(outer -> outer.getDirectCalls().stream()
                        .mapToInt(inner -> outer.addIndirectCalls(inner.getIndirectCalls()) ? 1 : 0)
                ).sum() > 0;    // sum to force visiting all items in the stream
    }

    // The call stack will be as deep as the deepest nesting level of function calls
    // We expect the number to be rather small (certainly under 10), so the list is quite appropriate here
    private final List<MindcodeFunction> callStack = new ArrayList<>();

    private void visitFunction(MindcodeFunction function, boolean trackUsages, int count) {
        // Needs to be called to update visited status
        function.updatePlacement(trackUsages ? count : 0);

        int index = callStack.indexOf(function);
        callStack.add(function);
        if (index >= 0) {
            // Detected a cycle in the call graph starting at index. Mark all calls on the cycle as recursive, stop DFS
            // We know function is now at the beginning and also at the end of the cycle in callStack
            MindcodeFunction caller = function;
            for (MindcodeFunction nextCallee : callStack.subList(index + 1, callStack.size())) {
                caller.addRecursiveCall(nextCallee);
                caller = nextCallee;
            }
        } else {
            // Visit all children
            // If a function is declared inline, its call count
            int multiplier = function.getDeclaration().isInline() ? count : 1;
            function.getCallCardinality().forEach((f, calls) -> visitFunction(f, trackUsages, multiplier * calls));
        }
        callStack.removeLast();
    }

    private void validateFunction(MindcodeFunction function) {
        if (function.getDeclaration().isInline() && function.isRecursive()) {
            error(function.getSourcePosition(), ERR.FUNCTION_RECURSIVE_INLINE, function.getName());
        }

        if (!function.getDeclaration().isInline() && function.isVarargs()) {
            error(function.getSourcePosition(), ERR.FUNCTION_VARARGS_NOT_INLINE, function.getName());
        }

        if (profile.getSyntacticMode() != SyntacticMode.STRICT) {
            function.getDeclaredParameters().stream()
                    .filter(p -> processor.isBlockName(p.getName()))
                    .forEach(p -> error(p, ERR.PARAMETER_NAME_RESERVED_LINKED, p.getName(), function.getName()));

            function.getDeclaredParameters().stream()
                    .filter(p -> processor.isGlobalName(p.getName()))
                    .forEach(p -> error(p, ERR.PARAMETER_NAME_RESERVED_GLOBAL, p.getName(), function.getName()));
        }
    }
}
