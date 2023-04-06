package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.ControlBlockAstNode;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.ast.StackAllocation;
import info.teksol.mindcode.ast.VarRef;
import info.teksol.mindcode.mindustry.generator.CallGraph.Function;
import info.teksol.mindcode.mindustry.instructions.InstructionProcessor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static info.teksol.mindcode.mindustry.generator.CallGraph.MAIN;

public class CallGraphCreator  {
    private final Deque<String> declarationStack = new ArrayDeque<>();
    private final Map<String, FunctionDeclaration> functions = new HashMap<>();
    private final Map<String, List<String>> callMap = new HashMap<>();
    private final List<AstNode> encounteredNodes = new ArrayList<>();
    private String activeFunction = MAIN;
    private StackAllocation allocatedStack;


    private CallGraphCreator() {
        callMap.put(MAIN, new ArrayList<>());
    }

    public static CallGraph createFunctionGraph(Seq program, InstructionProcessor instructionProcessor) {
        return new CallGraphCreator().buildFunctionGraph(program, instructionProcessor);
    }

    private CallGraph buildFunctionGraph(Seq program, InstructionProcessor instructionProcessor) {
        visitNode(program);
        encounteredNodes.clear();

        // Creating function structure
        CallGraph graph = new CallGraph(allocatedStack);
        functions.values().forEach(graph::addFunction);
        callMap.forEach(graph::addFunctionCalls);

        graph.buildCallGraph(instructionProcessor);

        graph.getFunctions().forEach(f -> validateFunction(instructionProcessor, f));

        return graph;
    }

    private void validateFunction(InstructionProcessor instructionProcessor, Function function) {
        if (function.isInline() && function.isRecursive()) {
            throw new InlineRecursiveFunctionException("Recursive function declared inline: " + function.getName());
        }

        function.getParams().stream()
                .map(VarRef::getName)
                .filter(instructionProcessor::isBlockName)
                .forEach(name -> { 
                    throw new InvalidParameterNameException("Function " + function.getName()
                        + " has parameter named " + name + "; this name is reserved for linked blocks");
                });

        function.getParams().stream()
                .map(VarRef::getName)
                .filter(instructionProcessor::isGlobalName)
                .forEach(name -> {
                    throw new InvalidParameterNameException("Function " + function.getName()
                        + " has parameter named " + name + "; this name denotes global variable");
                });
    }
    
    private void visitNode(AstNode nodeToVisit) {
        AstNode node = nodeToVisit;     // TODO: constant evaluation
        encounteredNodes.add(node);

        if (node instanceof FunctionCall) {
            visitFunctionCall((FunctionCall) node);
        } else if (node instanceof FunctionDeclaration) {
            visitFunctionDeclaration((FunctionDeclaration) node);
        } else if (node instanceof StackAllocation) {
            visitStackAllocation((StackAllocation) node);
        } else {
            node.getChildren().forEach(this::visitNode);
        };
    }

    private void visitFunctionCall(FunctionCall functionCall) {
        callMap.get(activeFunction).add(functionCall.getFunctionName());
        functionCall.getParams().forEach(this::visitNode);
    }

    private void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        if (functions.containsKey(functionDeclaration.getName())) {
            throw new GenerationException("Multiple declarations of function " + functionDeclaration.getName());
        }
        functions.put(functionDeclaration.getName(), functionDeclaration);
        callMap.put(functionDeclaration.getName(), new ArrayList<>());
        declarationStack.push(activeFunction);
        activeFunction = functionDeclaration.getName();
        visitNode(functionDeclaration.getBody());
        activeFunction = declarationStack.pop();
    }

    private void  visitStackAllocation(StackAllocation node) {
        if (allocatedStack != null) {
            throw new GenerationException("Found a second stack allocation in " + node);
        }

        if (encounteredNodes.stream().anyMatch(n -> n instanceof ControlBlockAstNode)) {
            throw new MisplacedStackAllocationException(
                    "Stack allocation must not be preceded by a control statement or a function call.");
        }

        allocatedStack = node;
    }
}
