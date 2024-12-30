package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.generated.ast.ComposedAstNodeVisitor;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstFunctionDeclaration;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstProgram;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.generation.builders.*;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CodeGeneratorContext context;
    private final CallGraph callGraph;
    private final CompileTimeEvaluator evaluator;
    private final CodeAssembler assembler;
    private final Variables variables;

    private final ComposedAstNodeVisitor<ValueStore> nodeVisitor;
    private final FunctionDeclarationsBuilder functionCompiler;

    public CodeGenerator(CodeGeneratorContext context) {
        super(context.messageConsumer());
        this.context = context;
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

    public void generateCode(AstProgram program) {
        // TODO In relaxed syntax, top context is the main body
        //      In strict syntax, top context is the root context
        variables.enterFunction(callGraph.getMain(), List.of());
        assembler.enterAstNode(program);
        context.setTopAstContext(assembler.getAstContext());

        visit(program, false);

        assembler.exitAstNode(program);
        variables.exitFunction(callGraph.getMain());

        // Check stack allocations
        if (!context.stackTracker().isValid()) {
            callGraph.recursiveFunctions().filter(MindcodeFunction::isUsed).forEach(f -> error(f.getDeclaration(),
                    "Function '%s' is recursive and no stack was allocated.", f.getName()));
        }

        // Separate main program from function declarations
        assembler.createCompilerEnd();

        // Create functions from function declarations
        callGraph.getFunctions().stream().filter(f -> !f.isMain()).forEach(functionCompiler::compileFunction);
    }

    public ValueStore visit(AstMindcodeNode node, boolean evaluate) {
        // Completely skip function declarations to prevent creating AST contexts for them
        if (node instanceof AstFunctionDeclaration) {
            return LogicVoid.VOID;
        }

        assembler.enterAstNode(node);
        variables.enterAstNode();
        ValueStore result = nodeVisitor.visit(evaluator.evaluate(node));
        variables.exitAstNode();
        assembler.exitAstNode(node);
        return result;
    }
}
