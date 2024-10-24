package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ast.*;
import info.teksol.mindcode.compiler.generator.CallGraph.LogicFunction;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;

import java.util.*;
import java.util.function.Consumer;

import static info.teksol.mindcode.compiler.generator.CallGraph.MAIN;

public class CallGraphCreator extends AbstractMessageEmitter {
    private final Deque<String> declarationStack = new ArrayDeque<>();
    private final Map<String, FunctionDeclaration> functions = new HashMap<>();
    private final Map<String, List<String>> callMap = new HashMap<>();
    private final Set<String> syncedVariables = new HashSet<>();
    private final List<AstNode> encounteredNodes = new ArrayList<>();
    private String activeFunction = MAIN;
    private StackAllocation allocatedStack;

    private CallGraphCreator(Consumer<MindcodeMessage> messageConsumer) {
        super(messageConsumer);
        callMap.put(MAIN, new ArrayList<>());
    }

    public static CallGraph createFunctionGraph(Seq program, Consumer<MindcodeMessage> messageConsumer,
            InstructionProcessor instructionProcessor) {
        return new CallGraphCreator(messageConsumer).buildFunctionGraph(program, instructionProcessor);
    }

    private CallGraph buildFunctionGraph(Seq program, InstructionProcessor instructionProcessor) {
        visitNode(program);
        encounteredNodes.clear();

        // Creating function structure
        CallGraph graph = new CallGraph(allocatedStack);
        functions.values().forEach(graph::addFunction);
        callMap.forEach(graph::addFunctionCalls);
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
        encounteredNodes.add(nodeToVisit);

        if (nodeToVisit instanceof FunctionCall n) {
            visitFunctionCall(n);
        } else if (nodeToVisit instanceof FunctionDeclaration n) {
            visitFunctionDeclaration(n);
        } else if (nodeToVisit instanceof StackAllocation n) {
            visitStackAllocation(n);
        } else {
            nodeToVisit.getChildren().forEach(this::visitNode);
        }
    }

    private void visitFunctionCall(FunctionCall functionCall) {
        callMap.get(activeFunction).add(functionCall.getFunctionName());

        if (functionCall.getFunctionName().equals("sync")
                && functionCall.getParams().size() == 1
                && functionCall.getParams().get(0) instanceof VarRef var
                && var.getName().equals(var.getName().toUpperCase())) {
            syncedVariables.add(var.getName());
        }

        functionCall.getParams().forEach(this::visitNode);
    }

    private void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        if (functions.containsKey(functionDeclaration.getName())) {
            error(functionDeclaration.getInputPosition(),
                    "Multiple declarations of function '%s'.", functionDeclaration.getName());
        }
        functions.put(functionDeclaration.getName(), functionDeclaration);
        callMap.put(functionDeclaration.getName(), new ArrayList<>());
        declarationStack.push(activeFunction);
        activeFunction = functionDeclaration.getName();
        visitNode(functionDeclaration.getBody());
        activeFunction = declarationStack.pop();
    }

    private void visitStackAllocation(StackAllocation node) {
        if (allocatedStack != null) {
            error(node.getInputPosition(), "Multiple stack allocations.");
        }

        if (encounteredNodes.stream().anyMatch(ControlBlockAstNode.class::isInstance)) {
            error(node.getInputPosition(),
                    "Stack allocation must not be preceded by a control statement or a function call.");
        }

        allocatedStack = node;
    }
}
