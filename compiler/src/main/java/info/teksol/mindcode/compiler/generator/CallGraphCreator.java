package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class CallGraphCreator extends AbstractMessageEmitter {
    private final FunctionDefinitions functions;
    private final Set<String> syncedVariables = new HashSet<>();
    private boolean foundControlBlock = false;
    private LogicFunction activeFunction;
    private StackAllocation allocatedStack;

    public static CallGraph createCallGraph(Seq program, Consumer<MindcodeMessage> messageConsumer,
            InstructionProcessor instructionProcessor) {
        return new CallGraphCreator(messageConsumer).buildFunctionGraph(program, instructionProcessor);
    }

    private CallGraphCreator(Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        functions = new FunctionDefinitions(messageConsumer);
        activeFunction = functions.getMain();
    }

    private CallGraph buildFunctionGraph(Seq program, InstructionProcessor instructionProcessor) {
        visitNode(program);

        functions.getFunctions().forEach(f -> f.initializeCalls(functions));

        // Creating function structure
        CallGraph graph = new CallGraph(functions, allocatedStack);
        graph.addSyncedVariables(syncedVariables);
        graph.buildCallGraph(instructionProcessor);
        graph.getFunctions().forEach(f -> validateFunction(instructionProcessor, f));
        return graph;
    }

    private void validateFunction(InstructionProcessor instructionProcessor, LogicFunction function) {
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
    
    private void visitNode(AstNode nodeToVisit) {
        if (nodeToVisit instanceof ControlBlockAstNode) {
            foundControlBlock = true;
        }

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

        if (foundControlBlock) {
            error(node.getInputPosition(),
                    "Stack allocation must not be preceded by a control statement or a function call.");
        }

        allocatedStack = node;
    }

    private void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        LogicFunction previousFunction = activeFunction;
        activeFunction = functions.addFunctionDeclaration(functionDeclaration);
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
}
