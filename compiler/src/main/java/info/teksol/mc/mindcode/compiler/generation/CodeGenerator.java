package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.generated.ast.ComposedAstNodeVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.generation.builders.*;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.SyntacticMode;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Supplier;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CodeGeneratorContext context;
    private final CompilerProfile profile;
    private final CallGraph callGraph;
    private final CompileTimeEvaluator evaluator;
    private final CodeAssembler assembler;
    private final Variables variables;

    private final ComposedAstNodeVisitor<ValueStore> nodeVisitor;
    private final FunctionDeclarationsBuilder functionCompiler;

    // Nesting level of code blocks
    private int nested = 0;

    public CodeGenerator(CodeGeneratorContext context) {
        super(context.messageConsumer());
        this.context = context;
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
        nodeVisitor.registerVisitor(new DeclarationsBuilder(this, context));
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
        variables.exitFunction(function);
        nested--;
    }

    public ValueStore processInLocalScope(Supplier<ValueStore> process) {
        nested++;
        ValueStore result = process.get();
        nested--;
        return result;
    }

    public void generateCode(AstProgram program) {
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
        if (node instanceof AstFunctionDeclaration) {
            return LogicVoid.VOID;
        }

        if (node.getScope() == AstNodeScope.LOCAL) nested++;
        if (node instanceof AstAllocations || node instanceof AstParameter) allowUndeclaredLinks = true;
        if (node instanceof AstConstant || node instanceof AstParameter) requireMlogConstant = true;
        assembler.enterAstNode(node);
        variables.enterAstNode();
        ValueStore result = nodeVisitor.visit(evaluator.evaluate(node, isLocalContext(), requireMlogConstant));
        variables.exitAstNode();
        assembler.exitAstNode(node);
        if (node instanceof AstConstant || node instanceof AstParameter) requireMlogConstant = false;
        if (node instanceof AstAllocations || node instanceof AstParameter) allowUndeclaredLinks = false;
        if (node.getScope() == AstNodeScope.LOCAL) nested--;

        // Reset global scope warning after exiting code block.
        if (node instanceof AstCodeBlock) codeInGlobalScopeWarning = false;

        return result;
    }
}
