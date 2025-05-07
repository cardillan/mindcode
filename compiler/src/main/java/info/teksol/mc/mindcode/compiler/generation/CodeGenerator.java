package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.generated.ast.ComposedAstNodeVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.CallType;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.Modifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.generation.builders.*;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.mindcode.logic.instructions.DrawInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.PrintingInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CodeGeneratorContext context;
    private final AstProgram program;
    private final CompilerProfile profile;
    private final CallGraph callGraph;
    private final CompileTimeEvaluator evaluator;
    private final CodeAssembler assembler;
    private final Variables variables;

    private final ComposedAstNodeVisitor<ValueStore> nodeVisitor;
    private final FunctionDeclarationsBuilder functionCompiler;
    private final DeclarationsBuilder declarationsBuilder;

    private LogicLabel remoteWaitLabel = LogicLabel.INVALID;

    // Nesting level of code blocks
    private int nested = 0;

    public static void generateCode(CodeGeneratorContext context, AstProgram program) {
        CodeGenerator codeGenerator = new CodeGenerator(context, program);
        codeGenerator.generateCode();
    }

    private CodeGenerator(CodeGeneratorContext context, AstProgram program) {
        super(context.messageConsumer());
        this.context = context;
        this.program = program;
        profile = context.compilerProfile();
        callGraph = context.callGraph();
        evaluator = context.compileTimeEvaluator();
        assembler = context.assembler();
        variables = context.variables();

        nodeVisitor = new ComposedAstNodeVisitor<>(node -> {
            throw new MindcodeInternalError("Unhandled node " + node);
        });


        // Registration order is unimportant as long as multiple handlers do not handle the same node
        nodeVisitor.registerVisitor(new AssignmentsBuilder(this, context));
        nodeVisitor.registerVisitor(new BreakContinueStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new CaseExpressionsBuilder(this, context));
        nodeVisitor.registerVisitor(declarationsBuilder = new DeclarationsBuilder(this, context));
        nodeVisitor.registerVisitor(new ForEachLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new FunctionCallsBuilder(this, context));
        nodeVisitor.registerVisitor(new IdentifiersBuilder(this, context));
        nodeVisitor.registerVisitor(new IfExpressionsBuilder(this, context));
        nodeVisitor.registerVisitor(new IteratedForLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new LiteralsBuilder(this, context));
        nodeVisitor.registerVisitor(new MemberAccessBuilder(this, context));
        nodeVisitor.registerVisitor(new OperatorsBuilder(this, context));
        nodeVisitor.registerVisitor(new RangedForLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new StatementListsBuilder(this, context));
        nodeVisitor.registerVisitor(new WhileLoopStatementsBuilder(this, context));

        functionCompiler = new FunctionDeclarationsBuilder(this, context);
    }

    public boolean isLocalContext() {
        return nested > 0;
    }

    protected void enterFunction(MindcodeFunction function, List<FunctionArgument> varargs) {
        nested++;
        variables.enterFunction(function, varargs);
    }

    protected void exitFunction(MindcodeFunction function) {
        evaluator.purgeFromCache(function.getDeclaration().getBody());
        variables.exitFunction(function);
        nested--;
    }

    public ValueStore processInLocalScope(Supplier<ValueStore> process) {
        nested++;
        ValueStore result = process.get();
        nested--;
        return result;
    }

    private void generateCode() {
        variables.enterFunction(callGraph.getMain(), List.of());
        assembler.enterAstNode(program);
        visit(program, false);
        assembler.exitAstNode(program);
        variables.exitFunction(callGraph.getMain());

        // Check stack allocations
        if (!context.stackTracker().isValid()) {
            callGraph.recursiveFunctions().filter(MindcodeFunction::isUsed).forEach(f -> error(f.getDeclaration().getIdentifier(),
                    ERR.FUNCTION_RECURSIVE_NO_STACK, f.getName()));
        }

        // Separate main program from function declarations
        assembler.createCompilerEnd();

        // From now on, we'll only compile function bodies, which is always a local context.
        nested++;

        // Create functions from function declarations
        callGraph.getFunctions().stream().filter(f -> !f.isMain()).forEach(functionCompiler::compileFunction);

        // Restore back just in case
        nested--;

        addMissingPrintflush();
    }

    public  LogicLabel getRemoteWaitLabel() {
        return remoteWaitLabel;
    }

    /// Generates an error if the current target doesn't support remote calls or variables
    void verifyMinimalRemoteTarget(AstMindcodeNode node) {
        if (!profile.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(node, ERR.REMOTE_REQUIRES_TARGET_8);
        }
    }

    private void generateRemoteInitialization() {
        assembler.setContextType(program, AstContextType.INIT, AstSubcontextType.REMOTE_INIT);
        callGraph.getFunctions().stream()
                .filter(f -> f.isRemote() && f.isEntryPoint())
                .forEach(this::setupRemoteFunctionAddress);
        assembler.createSet(LogicVariable.INITIALIZED, LogicBoolean.TRUE);
        assembler.clearContextType(program);

        assembler.setContextType(program, AstContextType.LOOP, AstSubcontextType.BASIC);
        assembler.setSubcontextType(AstSubcontextType.FLOW_CONTROL, 1.0);
        remoteWaitLabel = assembler.nextLabel().withoutStateTransfer();
        assembler.createLabel(remoteWaitLabel);
        assembler.setSubcontextType(AstSubcontextType.BODY, 1.0);
        findBackgroundProcessFunction().ifPresent(functionCompiler::placeFunctionBody);
        assembler.suspendExecution();
        assembler.setSubcontextType(AstSubcontextType.CONDITION, 1.0);
        assembler.createJumpUnconditional(remoteWaitLabel);
        assembler.clearSubcontextType();
        assembler.clearContextType(program);
    }

    private void setupRemoteFunctionAddress(MindcodeFunction function) {
        if (profile.isSymbolicLabels()) {
            assembler.createJumpUnconditional(LogicLabel.symbolic("*setAddr-" + function.getName()).withoutStateTransfer());
            assembler.createLabel(LogicLabel.symbolic("*retAddr-" + function.getName()).withoutStateTransfer());
        } else {
            assembler.createSetAddress(LogicVariable.fnAddress(function, null), Objects.requireNonNull(function.getLabel()));
        }
    }

    private Optional<MindcodeFunction> findBackgroundProcessFunction() {
        return callGraph.getFunctions().stream()
                .filter(f -> f.getDeclaration().getCallType() == CallType.NONE && !f.isRecursive() &&
                             f.isVoid() && f.getParameters().isEmpty() && f.getName().equals("backgroundProcess"))
                .findFirst();
    }

    private @Nullable AstContext mainBodyContext;
    private int mainBodyEndIndex;

    private void addMissingPrintflush() {
        List<LogicInstruction> program = assembler.getInstructions();
        if (!profile.isAutoPrintflush()
                || program.stream().noneMatch(ix -> ix instanceof PrintingInstruction)
                || program.stream().anyMatch(ix -> ix.getOpcode() == Opcode.PRINTFLUSH
                || ix instanceof DrawInstruction dix && dix.getType().getKeyword().equals("print"))) {
            return;
        }

        program.add(mainBodyEndIndex,
                context.instructionProcessor().createPrintflush(Objects.requireNonNull(mainBodyContext),
                        LogicVariable.block(SourcePosition.EMPTY, "message1")));

        warn(WARN.MISSING_PRINTFLUSH_ADDED);
    }

    // `true` if a warning about code in global scope was emitted
    private boolean codeInGlobalScopeWarning = false;

    private void emitWrongScopeMessage(AstMindcodeNode node) {
        if (!isLocalContext()) {
            if (codeInGlobalScopeWarning && !node.reportAllScopeErrors()) return;
            codeInGlobalScopeWarning = true;
        }

        if (profile.getSyntacticMode() != SyntacticMode.RELAXED || node.reportAllScopeErrors()) {
            String message = isLocalContext()
                    ? ERR.SCOPE_DECLARATION_WITHIN_CODE_BLOCK
                    : ERR.SCOPE_CODE_OUTSIDE_CODE_BLOCK;

            if (profile.getSyntacticMode() == SyntacticMode.STRICT || node.reportAllScopeErrors()) {
                error(node, "%s", message);
            } else {
                warn(node, "%s", message);
            }
        }
    }

    public boolean allowUndeclaredLinks() {
        return allowUndeclaredLinks;
    }

    boolean allowUndeclaredLinks = false;
    boolean requireMlogConstant = false;

    public ValueStore visit(AstMindcodeNode node, boolean evaluate) {
        if (node.getScopeRestriction().disallowed(isLocalContext() ? AstNodeScope.LOCAL : AstNodeScope.GLOBAL)) {
            emitWrongScopeMessage(node);
        }

        // Completely skip function declarations to prevent creating AST contexts for them
        if (node instanceof AstFunctionDeclaration declaration) {
            if (declaration.isRemote()) verifyMinimalRemoteTarget(node);
            return LogicVoid.VOID;
        }

        // Skip remote modules
        if (node instanceof AstModule module && module.getRemoteProcessor() != null) {
            createRemoteVariables(module);
            return LogicVoid.VOID;
        }

        if (node.getScope() == AstNodeScope.LOCAL) nested++;
        if (node instanceof AstAllocations || node instanceof AstParameter || node instanceof AstRequire) allowUndeclaredLinks = true;
        if (node instanceof AstConstant || node instanceof AstParameter) requireMlogConstant = true;
        assembler.enterAstNode(node);
        variables.enterAstNode();
        ValueStore result = nodeVisitor.visit(evaluator.evaluate(node, isLocalContext(), requireMlogConstant));

        if (node == program.getMainModule()) {
            mainBodyContext = assembler.getAstContext();
            mainBodyEndIndex = assembler.getInstructions().size();
            if (!program.isMainProgram()) {
                generateRemoteInitialization();
            }
        }

        variables.exitAstNode();
        assembler.exitAstNode(node);
        if (node instanceof AstConstant || node instanceof AstParameter) requireMlogConstant = false;
        if (node instanceof AstAllocations || node instanceof AstParameter || node instanceof AstRequire) allowUndeclaredLinks = false;
        if (node.getScope() == AstNodeScope.LOCAL) nested--;

        // Reset global scope warning after exiting code block.
        if (node instanceof AstCodeBlock) codeInGlobalScopeWarning = false;

        return result;
    }

    private void createRemoteVariables(AstModule module) {
        module.getChildren().stream()
                .filter(AstVariablesDeclaration.class::isInstance)
                .map(AstVariablesDeclaration.class::cast)
                .filter(n -> n.getModifiers().stream().anyMatch(m -> m.getModifier() == Modifier.REMOTE))
                .forEach(n -> declarationsBuilder.visitRemoteVariablesDeclaration(module, n));
    }
}
