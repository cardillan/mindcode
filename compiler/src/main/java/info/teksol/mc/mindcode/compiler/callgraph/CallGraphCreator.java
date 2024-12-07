package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class CallGraphCreator extends AbstractMessageEmitter {
    private final CompilerProfile profile;
    private final InstructionProcessor processor;
    private final FunctionDefinitions functionDefinitions;
    private final List<MindcodeFunction> functions = new ArrayList<>();
    private MindcodeFunction activeFunction;

    public static CallGraph createCallGraph(CallGraphCreatorContext context, AstProgram program) {
        return new CallGraphCreator(context).buildCallGraph(program);
    }

    private CallGraphCreator(CallGraphCreatorContext context) {
        super(context.messageConsumer());
        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        functionDefinitions = new FunctionDefinitions(messageConsumer);
        activeFunction = functionDefinitions.getMain();
    }

    private CallGraph buildCallGraph(AstProgram program) {
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
            case AstFunctionDeclaration n   -> visitFunctionDeclaration(n);
            case AstFunctionCall n          -> visitFunctionCall(n);
            default                         -> nodeToVisit.getChildren().forEach(this::visitNode);
        }
    }

    private void visitFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
        MindcodeFunction previousFunction = activeFunction;
        activeFunction = functionDefinitions.addFunctionDeclaration(functionDeclaration);
        functionDeclaration.getBody().forEach(this::visitNode);
        activeFunction = previousFunction;
    }

    private void visitFunctionCall(AstFunctionCall functionCall) {
        activeFunction.addCall(functionCall);
        functionCall.getArguments().forEach(this::visitNode);
    }

    /// Inspects the structure of function calls and sets function properties accordingly. Assigns a function prefix
    /// and labels to recursive and stackless functions.
    void buildCallTree() {
        // Walk the call tree
        visitFunction(functionDefinitions.getMain(), 0);

        // 1st level of indirect calls
        functions.forEach(outer ->
                outer.getCallCardinality().keySet().forEach(inner ->
                        outer.addIndirectCalls(inner.getDirectCalls())
                )
        );

        // 2nd and other levels of indirection
        // Must end eventually, as there's a finite number of functions to add
        while (propagateIndirectCalls()) ;

        functions.stream().filter(f -> !f.isMain() && !f.isInline()).forEach(this::setupOutOfLineFunction);
    }

    private void setupOutOfLineFunction(MindcodeFunction function) {
        function.setLabel(processor.nextLabel());
        function.setPrefix(processor.nextFunctionPrefix());
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

    private void visitFunction(MindcodeFunction function, int count) {
        function.markUsage(count);

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
            function.getCallCardinality().forEach(this::visitFunction);
        }
        callStack.removeLast();
    }

    private void validateFunction(MindcodeFunction function) {
        if (function.getDeclaration().isInline() && function.isRecursive()) {
            error(function.getSourcePosition(), "Recursive function '%s' declared 'inline'.", function.getName());
        }

        if (profile.getSyntacticMode() != SyntacticMode.STRICT) {
            function.getDeclaredParameters().stream()
                    .map(AstFunctionParameter::getName)
                    .filter(processor::isBlockName)
                    .forEach(name -> error(function.getSourcePosition(),
                            "Parameter '%s' of function '%s' uses name reserved for linked blocks.", name, function.getName()));

            function.getDeclaredParameters().stream()
                    .map(AstFunctionParameter::getName)
                    .filter(processor::isGlobalName)
                    .forEach(name -> error(function.getSourcePosition(),
                            "Parameter '%s' of function '%s' uses name reserved for global variables.", name, function.getName()));
        }
    }
}
