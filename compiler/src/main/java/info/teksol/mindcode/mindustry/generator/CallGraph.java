package info.teksol.mindcode.mindustry.generator;

import info.teksol.mindcode.ast.FunctionDeclaration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CallGraph {
    private final Map<String, Function> functions = new HashMap<>();

    public Function getFunction(String name) {
        return functions.get(name);
    }

    void addFunction(FunctionDeclaration declaration) {
        functions.put(declaration.getName(), new Function(declaration));
    }

    void addFunctionCalls(String name, Collection<String> functionCalls) {
        Function function = functions.get(name);
        functionCalls.stream().sorted().forEach(function::addCall);
    }

    void buildCallGraph(Collection<String> topLevelCalls) {
        topLevelCalls.forEach(this::visitFunction);
    }

    private final List<String> callStack = new ArrayList<>();

    private void visitFunction(String callee) {
        Function function = functions.get(callee);
        if (function == null) {
            // Unknown function. Probably a built-in one.
            return;
        }

        function.setUsed();

        int index = callStack.indexOf(callee);
        callStack.add(callee);
        if (index >= 0) {
            // Detected a cycle in the call graph starting at index. Mark all calls on the cycle as recursive, stop DFS
            // We know calee is now at the beginning and also the end of the cycle in callStack
            String caller = callee;
            for (String nextCallee : callStack.subList(index + 1, callStack.size())) {
                functions.get(caller).addRecursiveCall(nextCallee);
                caller = nextCallee;
            }
        } else {
            // Visit all children
            function.calls.forEach(this::visitFunction);
        }
        callStack.remove(callStack.size() - 1);
    }

    public class Function {
        private final FunctionDeclaration declaration;
        private final List<String> calls = new ArrayList<>();
        private final Set<String> recursiveCalls = new HashSet<>();
        private boolean used = false;

        Function(FunctionDeclaration declaration) {
            this.declaration = declaration;
        }

        public FunctionDeclaration getDeclaration() {
            return declaration;
        }

        public boolean isUsed() {
            return used;
        }

        public boolean isRecursive() {
            return !recursiveCalls.isEmpty();
        }

        public boolean isCallRecursive(String calledFunction) {
            return recursiveCalls.contains(calledFunction);
        }
        
        void setUsed() {
            used = true;
        }

        void addCall(String function) {
            calls.add(function);
        }

        void addRecursiveCall(String function) {
            recursiveCalls.add(function);
        }
    }
}
