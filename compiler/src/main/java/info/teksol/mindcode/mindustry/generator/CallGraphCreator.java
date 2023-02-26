package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.Seq;
import info.teksol.mindcode.ast.StackAllocation;
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

        // Creating function structure
        CallGraph graph = new CallGraph(allocatedStack);
        functions.values().forEach(graph::addFunction);
        callMap.forEach((k, v) -> graph.addFunctionCalls(k, v));

        graph.buildCallGraph(instructionProcessor);

        return graph;
    }
    
    private void visitNode(AstNode node) {
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
            throw new DuplicateStackAllocationException("Found a second stack allocation in " + node);
        }

        allocatedStack = node;
    }
}
