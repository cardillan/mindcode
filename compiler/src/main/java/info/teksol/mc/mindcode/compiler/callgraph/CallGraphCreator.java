package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class CallGraphCreator extends AbstractMessageEmitter {
    private final CompilerProfile globalProfile;
    private final InstructionProcessor processor;
    private final NameCreator nameCreator;
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
        this.globalProfile = context.globalCompilerProfile();
        this.processor = context.instructionProcessor();
        this.nameCreator = context.nameCreator();
        this.program = program;
        this.functionDefinitions = new FunctionDefinitions(messageConsumer, program.getMainModule());
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

        //functions.stream().filter(MindcodeFunction::isEntryPoint).forEach(this::printCallHierarchy);

        return new CallGraph(functionDefinitions);
    }

    void printCallHierarchy(MindcodeFunction function) {
        info("\nCall hierarchy for %s", function.isMain() ? "main code block" : declarationAsString(function));
        function.getDirectCalls().forEach(f -> printCallHierarchy(f, 1));
        info("\n");
        System.out.println(function.getName() + " -> " + function.getDirectCalls().stream().map(MindcodeFunction::getName).toList());
    }

    void printCallHierarchy(MindcodeFunction function, int depth) {
        info("%s%s", "    ".repeat(depth), declarationAsString(function));
        function.getDirectCalls().forEach(f -> printCallHierarchy(f, depth + 1));
    }

    private String declarationAsString(MindcodeFunction function) {
        AstFunctionDeclaration declaration = function.getDeclaration();
        StringBuilder sbr = new StringBuilder();
        Objects.requireNonNull(declaration);
        if (declaration.isInline()) sbr.append("inline ");
        if (declaration.isNoinline()) sbr.append("noinline ");
        sbr.append(declaration.getDataType() == DataType.VOID ? "void " : "def ");
        sbr.append(declaration.getName());
        sbr.append("(");
        boolean first = true;
        for (AstFunctionParameter parameter : declaration.getParameters()) {
            if (first) {
                first = false;
            } else {
                sbr.append(", ");
            }
            if (parameter.hasInModifier()) sbr.append("in ");
            if (parameter.hasOutModifier()) sbr.append("out ");
            sbr.append(parameter.getName().substring(parameter.getName().charAt(0) == '_' ? 1 : 0));
            if (parameter.isVarargs()) sbr.append("...");
        }
        sbr.append(")");

        return sbr.toString();
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

        if (activeModule.getRemoteProcessors().isEmpty()) {
            // Only process function bodies in non-remote modules
            MindcodeFunction previousFunction = activeFunction;
            activeFunction = functionDefinitions.addFunctionDeclaration(functionDeclaration, activeModule,
                    !program.isMainProgram() && functionDeclaration.isRemote() && activeModule.isMain());
            functionDeclaration.getBody().forEach(this::visitNode);
            activeFunction = previousFunction;
        } else if (functionDeclaration.isRemote()) {
            // Add remote functions in remote modules
            functionDefinitions.addFunctionDeclaration(functionDeclaration, activeModule, false);
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

        functions.stream().filter(MindcodeFunction::isRemote).forEach(this::setupOutOfLineFunction);
        functions.stream().filter(f -> f.isBackgroundProcess() || !f.isRemote() && !f.isMain() && !f.isInline()).forEach(this::setupOutOfLineFunction);
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
        nameCreator.setupFunctionPrefix(function);
        function.createVariables(nameCreator);
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
            // We know the function is now at the beginning and also at the end of the cycle in callStack
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

        List<AstFunctionParameter> params = function.getDeclaredParameters();
        if (!params.isEmpty()) {
            // Find duplicated parameters
            Set<String> names = new HashSet<>();
            for (AstFunctionParameter p : params) {
                if (!names.add(p.getName())) {
                    error(p.getIdentifier(), ERR.VARIABLE_MULTIPLE_DECLARATIONS, p.getName());
                }
            }

            // Varargs
            params.subList(0, params.size() - 1).stream()
                    .filter(AstFunctionParameter::isVarargs)
                    .forEach(p -> error(p, ERR.PARAMETER_VARARGS_NOT_LAST, p.getName(), function.getName()));

            // Ref varargs
            if (params.getLast().isVarargs() && params.getLast().isReference()) {
                error(params.getLast(), ERR.PARAMETER_REF_VARARGS, params.getLast().getName(), function.getName());
            }

            // Ref but not inline
            if (!function.getDeclaration().isInline()) {
                params.stream()
                        .filter(AstFunctionParameter::isReference)
                        .forEach(p -> error(function.getSourcePosition(), ERR.PARAMETER_REF_NOT_INLINE, p.getName(),  function.getName()));
            }


            // Ref cannot be in or out (prevented by syntax, but checked anyway)
            params.stream()
                    .filter(p -> p.hasRefModifier() && (p.hasInModifier() || p.hasOutModifier()))
                    .forEach(p -> error(function.getSourcePosition(), ERR.PARAMETER_REF_NOT_INLINE, p.getName(),  function.getName()));
        }

        // When the module is not the main one, the syntax mode must be strict
        // Remote functions use strict mode per being declared in a module
        if (globalProfile.getSyntacticMode() != SyntacticMode.STRICT && function.getModule().isMain() && !function.isRemote()) {
            params.stream()
                    .filter(p -> processor.isBlockName(p.getName()))
                    .forEach(p -> error(p, ERR.PARAMETER_NAME_RESERVED_LINKED, p.getName(), function.getName()));

            params.stream()
                    .filter(p -> processor.isGlobalName(p.getName()))
                    .forEach(p -> error(p, ERR.PARAMETER_NAME_RESERVED_GLOBAL, p.getName(), function.getName()));
        }
    }
}
