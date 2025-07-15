package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.generated.ast.ComposedAstNodeVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.messages.WARN;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstContext;
import info.teksol.mc.mindcode.compiler.astcontext.AstContextType;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.generation.builders.*;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.NameCreator;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.DrawInstruction;
import info.teksol.mc.mindcode.logic.instructions.LogicInstruction;
import info.teksol.mc.mindcode.logic.instructions.PrintingInstruction;
import info.teksol.mc.mindcode.logic.opcodes.Opcode;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import info.teksol.mc.profile.BuiltinEvaluation;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import info.teksol.mc.util.CRC64;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static info.teksol.mc.common.SourcePosition.EMPTY;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CodeGeneratorContext context;
    private final AstProgram program;
    private final CompilerProfile globalProfile;
    private final CallGraph callGraph;
    private final CompileTimeEvaluator evaluator;
    private final CodeAssembler assembler;
    private final Variables variables;

    private final ComposedAstNodeVisitor<ValueStore> nodeVisitor;
    private final FunctionDeclarationsBuilder functionCompiler;

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
        this.globalProfile = context.globalCompilerProfile();
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
        nodeVisitor.registerVisitor(new DeclarationsBuilder(this, context));
        nodeVisitor.registerVisitor(new ForEachLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new FunctionCallsBuilder(this, context));
        nodeVisitor.registerVisitor(new IdentifiersBuilder(this, context));
        nodeVisitor.registerVisitor(new IfExpressionsBuilder(this, context));
        nodeVisitor.registerVisitor(new IteratedForLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new LiteralsBuilder(this, context));
        nodeVisitor.registerVisitor(new MemberAccessBuilder(this, context));
        nodeVisitor.registerVisitor(new MlogBlocksBuilder(this, context));
        nodeVisitor.registerVisitor(new OperatorsBuilder(this, context));
        nodeVisitor.registerVisitor(new RangedForLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new StatementListsBuilder(this, context));
        nodeVisitor.registerVisitor(new WhileLoopStatementsBuilder(this, context));

        functionCompiler = new FunctionDeclarationsBuilder(this, context);
    }

    public NameCreator nameCreator() {
        return variables.nameCreator();
    }

    public CompilerProfile getGlobalProfile() {
        return globalProfile;
    }

    public String createRemoteSignature(Stream<AstFunctionDeclaration> functions) {
        String text = functions
                .sorted(Comparator.comparing(AstFunctionDeclaration::getName))
                .map(AstFunctionDeclaration::toSourceCode)
                .collect(Collectors.joining("\n"));

        return Long.toHexString(CRC64.hash1(text)) + ":" + MindcodeCompiler.REMOTE_PROTOCOL_VERSION;
    }

    public String createRemoteSignature(AstModule module) {
        return createRemoteSignature(callGraph.getFunctions().stream()
                .filter(f -> f.getModule() == module)
                .map(MindcodeFunction::getDeclaration));
    }

    public AstModule getModule(AstRequire node) {
        return context.getModule(node);
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

    public <T> T processInLocalScope(Supplier<T> process) {
        nested++;
        T result = process.get();
        nested--;
        return result;
    }

    private void generateCode() {
        if (!program.isMainProgram()) generateRemoteJumpTable();
        if (globalProfile.isTargetGuard()) generateTargetGuard();

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

        // Separate the main program from function declarations
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
        if (!globalProfile.getProcessorVersion().atLeast(ProcessorVersion.V8A)) {
            error(node, ERR.REMOTE_REQUIRES_TARGET_8);
        }
    }

    private void generateRemoteJumpTable() {
        List<MindcodeFunction> remoteFunctions = callGraph.assignRemoteFunctionIndexes(f -> f.isRemote() && f.isEntryPoint());
        if (remoteFunctions.isEmpty()) return;

        assembler.setContextType(program, AstContextType.JUMPS, AstSubcontextType.REMOTE_INIT);
        LogicLabel jumpTableEndLabel = assembler.nextLabel().withoutStateTransfer();
        assembler.createJumpUnconditional(jumpTableEndLabel);
        remoteFunctions.stream().map(function -> Objects.requireNonNull(function.getLabel())).forEach(assembler::createJumpUnconditional);
        assembler.createLabel(jumpTableEndLabel);
        assembler.clearContextType(program);
    }

    private void generateTargetGuard() {
        assembler.setContextType(program, AstContextType.INIT, AstSubcontextType.INIT);
        LogicLabel guardLabel = assembler.nextLabel().withoutStateTransfer();
        assembler.createLabel(guardLabel);
        switch (globalProfile.getProcessorVersion()) {
            case V6 -> {
                if (globalProfile.getBuiltinEvaluation() == BuiltinEvaluation.FULL) {
                    // Full
                    assembler.createJump(guardLabel, Condition.GREATER_THAN, new LogicToken("%FFFFFF"), LogicNumber.ZERO).setTargetGuard(true);
                } else {
                    // Compatible
                    // Do nothing. Everything is compatible with V6.
                }
            }
            case V7, V7A -> {
                if (globalProfile.getBuiltinEvaluation() == BuiltinEvaluation.FULL) {
                    // Full
                    assembler.createJump(guardLabel, Condition.NOT_EQUAL, new LogicToken("@blockCount"), LogicNumber.create(254)).setTargetGuard(true);
                } else {
                    // Compatible
                    assembler.createJump(guardLabel, Condition.STRICT_EQUAL, new LogicToken("%FFFFFF"), LogicNull.NULL).setTargetGuard(true);
                }
            }
            // TODO Handle
            case V8A, V8B -> {
                // No distinction between full and compatible: there are no other compatible versions besides V8
                assembler.createJump(guardLabel, Condition.STRICT_EQUAL, new LogicToken("%[red]"), LogicNull.NULL).setTargetGuard(true);
            }
            default -> throw new MindcodeInternalError("Unhandled processor version " + globalProfile.getProcessorVersion());
        }
        assembler.clearContextType(program);
    }

    private void setupRemoteSignature() {
        List<MindcodeFunction> remoteFunctions = callGraph.getFunctions().stream().filter(f -> f.isRemote() && f.isEntryPoint()).toList();
        assembler.setContextType(program, AstContextType.INIT, AstSubcontextType.REMOTE_INIT);
        String remoteSignature = createRemoteSignature(remoteFunctions.stream().map(MindcodeFunction::getDeclaration));
        assembler.createSet(LogicVariable.preserved(nameCreator().remoteSignature()), LogicString.create(remoteSignature));
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

    private Optional<MindcodeFunction> findBackgroundProcessFunction() {
        return callGraph.getFunctions().stream().filter(MindcodeFunction::isBackgroundProcess).findFirst();
    }

    private @Nullable AstContext mainBodyContext;
    private int mainBodyEndIndex;

    private void addMissingPrintflush() {
        List<LogicInstruction> program = assembler.getInstructions();
        if (!globalProfile.isAutoPrintflush()
                || program.stream().noneMatch(ix -> ix instanceof PrintingInstruction)
                || program.stream().anyMatch(ix -> ix.getOpcode() == Opcode.PRINTFLUSH
                || ix instanceof DrawInstruction dix && dix.getType().getKeyword().equals("print"))) {
            return;
        }

        program.add(mainBodyEndIndex,
                context.instructionProcessor().createPrintflush(Objects.requireNonNull(mainBodyContext),
                        LogicVariable.block(EMPTY, "message1")));

        warn(WARN.MISSING_PRINTFLUSH_ADDED);
    }

    // `true` if a warning about code in global scope was emitted
    private boolean codeInGlobalScopeWarning = false;

    private void emitWrongScopeMessage(AstMindcodeNode node) {
        if (!isLocalContext()) {
            if (codeInGlobalScopeWarning && !node.reportAllScopeErrors()) return;
            codeInGlobalScopeWarning = true;
        }

        if (node.getProfile().getSyntacticMode() != SyntacticMode.RELAXED || node.reportAllScopeErrors()) {
            String message = isLocalContext()
                    ? ERR.SCOPE_DECLARATION_WITHIN_CODE_BLOCK
                    : ERR.SCOPE_CODE_OUTSIDE_CODE_BLOCK;

            if (node.getProfile().getSyntacticMode() == SyntacticMode.STRICT || node.reportAllScopeErrors()) {
                error(node, "%s", message);
            } else {
                warn(node, "%s", message);
            }
        }
    }

    public boolean allowUndeclaredLinks() {
        return allowUndeclaredLinks;
    }

    private boolean allowUndeclaredLinks = false;
    private boolean requireMlogConstant = false;

    public ValueStore visit(AstMindcodeNode node, boolean evaluate) {
        if (node.getScopeRestriction().disallowed(isLocalContext() ? AstNodeScope.LOCAL : AstNodeScope.GLOBAL)) {
            emitWrongScopeMessage(node);
        }

        // Completely skip function declarations to prevent creating AST contexts for them
        if (node instanceof AstFunctionDeclaration declaration) {
            if (declaration.isRemote()) verifyMinimalRemoteTarget(node);
            return LogicVoid.VOID;
        }

        // Do not compile code from remote modules
        if (node instanceof AstModule module && !module.getRemoteProcessors().isEmpty()) {
            return LogicVoid.VOID;
        }

        if (node.getScope() == AstNodeScope.LOCAL) nested++;
        if (node instanceof AstAllocations || node instanceof AstParameter || node instanceof AstRequire) allowUndeclaredLinks = true;
        if (node instanceof AstParameter || node instanceof AstVariablesDeclaration var && var.isConstantDeclaration()) requireMlogConstant = true;
        assembler.enterAstNode(node);
        variables.enterAstNode();
        ValueStore result = nodeVisitor.visit(evaluator.evaluate(node, isLocalContext(), requireMlogConstant));

        if (node == program.getMainModule()) {
            mainBodyContext = assembler.getAstContext();
            mainBodyEndIndex = assembler.getInstructions().size();
            if (!program.isMainProgram()) {
                setupRemoteSignature();
            }
        }

        variables.exitAstNode();
        assembler.exitAstNode(node);
        if (node instanceof AstParameter || node instanceof AstVariablesDeclaration var && var.isConstantDeclaration()) requireMlogConstant = false;
        if (node instanceof AstAllocations || node instanceof AstParameter || node instanceof AstRequire) allowUndeclaredLinks = false;
        if (node.getScope() == AstNodeScope.LOCAL) nested--;

        // Reset global scope warning after exiting a code block.
        if (node instanceof AstCodeBlock) codeInGlobalScopeWarning = false;

        return result;
    }
}
