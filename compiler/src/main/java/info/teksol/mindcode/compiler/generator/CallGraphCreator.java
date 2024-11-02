package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CallGraphCreator extends AbstractMessageEmitter {
    private final InstructionProcessor instructionProcessor;
    private final FunctionDefinitions functionDefinitions;
    private final List<LogicFunction> functions = new ArrayList<>();
    private final Set<String> syncedVariables = new HashSet<>();
    private LogicFunction activeFunction;
    private StackAllocation allocatedStack;

    public static CallGraph createCallGraph(Seq program, Consumer<MindcodeMessage> messageConsumer,
            InstructionProcessor instructionProcessor) {
        return new CallGraphCreator(instructionProcessor, messageConsumer).buildCallGraph(program);
    }

    private CallGraphCreator(InstructionProcessor instructionProcessor, Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        this.instructionProcessor = instructionProcessor;
        this.functionDefinitions = new FunctionDefinitions(messageConsumer);
        this.activeFunction = functionDefinitions.getMain();
    }

    private CallGraph buildCallGraph(Seq program) {
        // Create functions from the AST tree
        visitNode(program);

        // Setup individual functions
        functions.addAll(functionDefinitions.getFunctions());
        functions.forEach(f -> f.initializeCalls(functionDefinitions));

        buildCallTree();

        functions.forEach(this::validateFunction);

        return new CallGraph(functionDefinitions, syncedVariables, allocatedStack);
    }

    private void visitNode(AstNode nodeToVisit) {
        if (nodeToVisit instanceof StackAllocation n) {
            visitStackAllocation(n);
        } else if (nodeToVisit instanceof FunctionDeclaration n) {
            visitFunctionDeclaration(n);
        } else if (nodeToVisit instanceof FunctionCall n) {
            visitFunctionCall(n);
        } else {
            nodeToVisit.getChildren().forEach(this::visitNode);
        }
    }

    private void visitStackAllocation(StackAllocation node) {
        if (allocatedStack != null) {
            error(node.getInputPosition(), "Multiple stack allocations.");
        }

        if (!activeFunction.isMain()) {
            error(node.getInputPosition(), "Stack allocation must not be declared within a function.");
        }

        allocatedStack = node;
    }

    private void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        LogicFunction previousFunction = activeFunction;
        activeFunction = functionDefinitions.addFunctionDeclaration(functionDeclaration);
        visitNode(functionDeclaration.getBody());
        activeFunction = previousFunction;
    }

    private void visitFunctionCall(FunctionCall functionCall) {
        activeFunction.addCall(functionCall);

        if (functionCall.getFunctionName().equals("sync")
                && functionCall.getArguments().size() == 1
                && functionCall.getArguments().get(0).getExpression() instanceof VarRef var
                && var.getName().equals(var.getName().toUpperCase())) {
            syncedVariables.add(var.getName());
            warn(functionCall, "Variable '%s' is used as argument in the 'sync()' function, will be considered volatile.", var.getName());
        }

        functionCall.getArguments().forEach(this::visitNode);
    }

    /**
     * Inspects the structure of function calls and sets function properties accordingly. Assigns a function prefix
     * and labels to recursive and stackless functions.
     *
     * @param instructionProcessor processor used to obtain function prefixes and labels
     */
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

        functions.stream()
                .filter(f -> f.isUsed() && !f.isInline())
                .forEach(this::setupOutOfLineFunction);
    }

    private void setupOutOfLineFunction(LogicFunction function) {
        function.setLabel(instructionProcessor.nextLabel());
        function.setPrefix(instructionProcessor.nextFunctionPrefix());
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
    // We expect the number to be rather small (Certainly under 10), so the list is quite appropriate here
    private final List<LogicFunction> callStack = new ArrayList<>();

    private void visitFunction(LogicFunction function, int count) {
        function.markUsage(count);

        int index = callStack.indexOf(function);
        callStack.add(function);
        if (index >= 0) {
            // Detected a cycle in the call graph starting at index. Mark all calls on the cycle as recursive, stop DFS
            // We know function is now at the beginning and also at the end of the cycle in callStack
            LogicFunction caller = function;
            for (LogicFunction nextCallee : callStack.subList(index + 1, callStack.size())) {
                caller.addRecursiveCall(nextCallee);
                caller = nextCallee;
            }
        } else {
            // Visit all children
            function.getCallCardinality().forEach(this::visitFunction);
        }
        callStack.remove(callStack.size() - 1);
    }

    private void validateFunction(LogicFunction function) {
        if (function.getDeclaration().isInline() && function.isRecursive()) {
            error(function.getInputPosition(), "Recursive function '%s' declared 'inline'.", function.getName());
        }

        if (function.getName().contains("-")) {
            warn(function.getInputPosition(), "Function '%s': kebab-case identifiers are deprecated.", function.getName());
        }

        function.getDeclaredParameters().stream()
                .map(FunctionParameter::getName)
                .filter(instructionProcessor::isBlockName)
                .forEach(name -> error(function.getInputPosition(),
                        "Parameter '%s' of function '%s' uses name reserved for linked blocks.", name, function.getName()));

        function.getDeclaredParameters().stream()
                .map(FunctionParameter::getName)
                .filter(instructionProcessor::isGlobalName)
                .forEach(name -> error(function.getInputPosition(),
                        "Parameter '%s' of function '%s' uses name reserved for global variables.", name, function.getName()));
    }
}
