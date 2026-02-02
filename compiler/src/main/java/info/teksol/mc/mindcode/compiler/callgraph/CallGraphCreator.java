package info.teksol.mc.mindcode.compiler.callgraph;

import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.CompilerMessageEmitter;
import info.teksol.mc.mindcode.compiler.DataType;
import info.teksol.mc.mindcode.compiler.FunctionModifier;
import info.teksol.mc.mindcode.compiler.ToolMessageEmitter;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.GlobalCompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class CallGraphCreator extends CompilerMessageEmitter {
    private final ToolMessageEmitter toolMessages;
    private final GlobalCompilerProfile globalProfile;
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
        this.toolMessages = new ToolMessageEmitter(context.messageConsumer());
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

        // Set up individual functions
        functions.addAll(functionDefinitions.getFunctions());
        functions.forEach(f -> f.initializeCalls(functionDefinitions));

        buildCallTree();

        functions.forEach(this::validateFunction);

        //functions.stream().filter(MindcodeFunction::isEntryPoint).forEach(this::printCallHierarchy);

        return new CallGraph(functionDefinitions);
    }

    void printCallHierarchy(MindcodeFunction function) {
        toolMessages.info("\nCall hierarchy for %s", function.isMain() ? "main code block" : declarationAsString(function));
        function.getDirectCalls().forEach(f -> printCallHierarchy(f, 1));
        toolMessages.info("\n");
        System.out.println(function.getName() + " -> " + function.getDirectCalls().stream().map(MindcodeFunction::getName).toList());
    }

    void printCallHierarchy(MindcodeFunction function, int depth) {
        toolMessages.info("%s%s", "    ".repeat(depth), declarationAsString(function));
        function.getDirectCalls().forEach(f -> printCallHierarchy(f, depth + 1));
    }

    private String declarationAsString(MindcodeFunction function) {
        AstFunctionDeclaration declaration = function.getDeclaration();
        StringBuilder sbr = new StringBuilder();
        Objects.requireNonNull(declaration);
        declaration.getModifiers().forEach(m -> sbr.append(m.getModifier().keyword()).append(' '));
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

    private Set<FunctionModifier> getEffectiveModifiers(List<AstFunctionModifier> functionModifiers) {
        EnumSet<FunctionModifier> allModifiers = EnumSet.noneOf(FunctionModifier.class);
        EnumSet<FunctionModifier> modifiers = EnumSet.noneOf(FunctionModifier.class);

        for (AstFunctionModifier astModifier : functionModifiers) {
            FunctionModifier modifier = astModifier.getModifier();
            boolean invalid = false;
            for (FunctionModifier m : allModifiers) {
                if (!m.compatible(modifier)) {
                    error(astModifier, ERR.FUNCTION_INCOMPATIBLE_MODIFIER, modifier.keyword(), m.keyword());
                    invalid = true;
                }
            }

            if (modifier == FunctionModifier.REMOTE) {
                warn(astModifier, WARN.DEPRECATED_USE_OF_REMOTE);
            }

            allModifiers.add(modifier);
            if (!invalid) {
                modifiers.add(modifier);
            }
        }

        if (modifiers.remove(FunctionModifier.REMOTE)) {
            modifiers.add(FunctionModifier.EXPORT);
        }

        return modifiers;
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

        Set<FunctionModifier> modifiers = getEffectiveModifiers(functionDeclaration.getModifiers());
        boolean isExported = modifiers.contains(FunctionModifier.EXPORT);

        if (activeModule.getRemoteProcessors().isEmpty()) {
            // Only process function bodies in non-remote modules
            MindcodeFunction previousFunction = activeFunction;
            activeFunction = functionDefinitions.addFunctionDeclaration(functionDeclaration, modifiers, activeModule,
                    !program.isMainProgram() && isExported && activeModule.isMain());
            functionDeclaration.getBody().forEach(this::visitNode);
            activeFunction = previousFunction;
        } else if (isExported) {
            // Add remote functions in remote modules
            functionDefinitions.addFunctionDeclaration(functionDeclaration, modifiers, activeModule, false);
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

        functions.stream().filter(MindcodeFunction::isExport).forEach(this::setupOutOfLineFunction);
        functions.stream().filter(f -> f.isBackgroundProcess() || !f.isExport() && !f.isMain() && !f.isInline()).forEach(this::setupOutOfLineFunction);
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
        //noinspection StatementWithEmptyBody
        while (propagateIndirectCalls()) ;
    }

    private void setupOutOfLineFunction(MindcodeFunction function) {
        if (function.isExport()) {
            function.setRemoteLabel(processor.nextLabel());
        }
        if (function.isAtomic()) {
            function.setAtomicLabel(processor.nextLabel());
        }
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
            int multiplier = function.isDeclaredInline() ? count : 1;
            function.getCallCardinality().forEach((f, calls) -> visitFunction(f, trackUsages, multiplier * calls));
        }
        callStack.removeLast();
    }

    private void validateFunction(MindcodeFunction function) {
        if (function.isRecursive()) {
            if (function.isDeclaredInline()) {
                error(function.getSourcePosition(), ERR.FUNCTION_RECURSIVE_INLINE, function.getName());
            }

            if (function.hasModifier(FunctionModifier.EXPORT)) {
                error(function.getSourcePosition(), ERR.FUNCTION_EXPORT_RECURSIVE, function.getName());
            }
        }

        if (!function.isDeclaredInline() && function.isVarargs()) {
            error(function.getSourcePosition(), ERR.FUNCTION_VARARGS_NOT_INLINE, function.getName());
        }

        if (function.isDebug()) {
            if (!function.isVoid()) {
                error(function.getSourcePosition(), ERR.FUNCTION_DEBUG_MUST_BE_VOID);
            }

            if (function.getDeclaredParameters().stream().anyMatch(p -> p.isOutput() && !p.isInput())) {
                error(function.getSourcePosition(), ERR.FUNCTION_DEBUG_NO_OUTPUTS);
            }
        }

        if (function.hasModifier(FunctionModifier.EXPORT)) {
            if (!globalProfile.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
                error(function.getSourcePosition(), ERR.REMOTE_REQUIRES_TARGET_8);
            }

            if (function.getModule().isProgram()) {
                error(function.getSourcePosition(), ERR.FUNCTION_EXPORT_MAIN);
            }
        }

        if (function.hasModifier(FunctionModifier.ATOMIC) && !processor.getProcessorVersion().atLeast(ProcessorVersion.V8B)) {
            error(function.getDeclaration(), ERR.ATOMIC_REQUIRES_TARGET_81);
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
            if (!function.isDeclaredInline()) {
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
        if (globalProfile.getSyntacticMode() != SyntacticMode.STRICT && function.getModule().isMain() && !function.isExport()) {
            params.stream()
                    .filter(p -> processor.isBlockName(p.getName()))
                    .forEach(p -> error(p, ERR.PARAMETER_NAME_RESERVED_LINKED, p.getName(), function.getName()));

            params.stream()
                    .filter(p -> processor.isGlobalName(p.getName()))
                    .forEach(p -> error(p, ERR.PARAMETER_NAME_RESERVED_GLOBAL, p.getName(), function.getName()));
        }
    }
}
