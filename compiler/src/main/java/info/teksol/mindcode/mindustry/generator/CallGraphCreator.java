package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.AstNode;
import info.teksol.mindcode.ast.FunctionCall;
import info.teksol.mindcode.ast.FunctionDeclaration;
import info.teksol.mindcode.ast.Seq;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CallGraphCreator  {
    private static final String MAIN_PROGRAM_BODY = "";

    private final Deque<String> declarationStack = new ArrayDeque<>();
    private final Map<String, FunctionDeclaration> functions = new HashMap<>();
    private final Map<String, Set<String>> callMap = new HashMap<>();
    private String activeFunction = MAIN_PROGRAM_BODY;

    private CallGraphCreator() {
        callMap.put(MAIN_PROGRAM_BODY, new HashSet<>());
    }

    public static CallGraph createFunctionGraph(Seq program) {
        return new CallGraphCreator().buildFunctionGraph(program);
    }

    private CallGraph buildFunctionGraph(Seq program) {
        visitNode(program);
        Set<String> topLevelCalls = callMap.remove(MAIN_PROGRAM_BODY);

        // Creating function structure
        CallGraph graph = new CallGraph();
        functions.values().forEach(graph::addFunction);
        callMap.forEach((k, v) -> graph.addFunctionCalls(k, v));

        graph.buildCallGraph(topLevelCalls);

        return graph;
    }
    
    private void visitNode(AstNode node) {
        if (node instanceof FunctionCall) {
            visitFunctionCall((FunctionCall) node);
        } if (node instanceof FunctionDeclaration) {
            visitFunctionDeclaration((FunctionDeclaration) node);
        };

        node.getChildren().forEach(this::visitNode);
    }

    private void visitFunctionCall(FunctionCall functionCall) {
        callMap.get(activeFunction).add(functionCall.getFunctionName());
    }

    private void visitFunctionDeclaration(FunctionDeclaration functionDeclaration) {
        if (functions.containsKey(functionDeclaration.getName())) {
            throw new GenerationException("Multiple declarations of function " + functionDeclaration.getName());
        }
        functions.put(functionDeclaration.getName(), functionDeclaration);
        callMap.put(functionDeclaration.getName(), new HashSet<>());
        declarationStack.push(activeFunction);
        activeFunction = functionDeclaration.getName();
        visitNode(functionDeclaration.getBody());
        activeFunction = declarationStack.pop();
    }
}
