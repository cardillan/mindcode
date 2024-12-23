package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.generated.ast.ComposedAstNodeVisitor;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.generator.AstSubcontextType;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstProgram;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mindcode.v3.compiler.generation.builders.*;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import info.teksol.mindcode.v3.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CodeGeneratorContext context;
    private final CallGraph callGraph;
    private final CompileTimeEvaluator evaluator;
    private final Assembler assembler;
    private final Variables variables;

    private final FunctionMapper functionMapper;
    private final ComposedAstNodeVisitor<NodeValue> nodeVisitor;

    // Stateful instances

    public CodeGenerator(CodeGeneratorContext context) {
        super(context.messageConsumer());
        this.context = context;
        callGraph = context.callGraph();
        evaluator = context.compileTimeEvaluator();
        assembler = context.assembler();
        variables = context.variables();

        functionMapper = FunctionMapperFactory.getFunctionMapper(context.instructionProcessor(),
                assembler::getAstContext, messageConsumer);

        nodeVisitor = new ComposedAstNodeVisitor<>(node -> { throw new MindcodeInternalError("Unhandled node " + node); });

        // Registration order is unimportant as long as multiple handlers do not handle the same node
        nodeVisitor.registerVisitor(new AssignmentsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new BreakContinueStatementsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new CaseExpressionsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new DeclarationsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new ForEachLoopStatementsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new IdentifiersBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new IfExpressionsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new IteratedForLoopStatementsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new LiteralsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new OperatorsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new RangedForLoopStatementsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new StatementListsBuilder(context, this::visit));
        nodeVisitor.registerVisitor(new WhileLoopStatementsBuilder(context, this::visit));
    }

    public void generateCode(AstProgram program) {
        // TODO In relaxed syntax, top context is the main body
        //      In strict syntax, top context is the root context
        variables.enterFunction(callGraph.getMain(), "", List.of());
        context.setTopAstContext(assembler.getAstContext());

        visit(program);

        variables.exitFunction(callGraph.getMain());

        // Check stack allocations
        if (context.stackAllocation() == null) {
            callGraph.recursiveFunctions().filter(LogicFunction::isUsed).forEach(f -> error(f.getDeclaration(),
                    "Function '%s' is recursive and no stack was allocated.", f.getName()));
        }

        // Process function declarations
        assembler.setSubcontextType(AstSubcontextType.END, 1.0);
        assembler.createEnd();
        assembler.clearSubcontextType();
    }

    private NodeValue visit(AstMindcodeNode node) {
        assembler.enterAstNode(node);
        variables.enterAstNode();
        NodeValue result = nodeVisitor.visit(evaluator.evaluate(node));
        variables.exitAstNode();
        assembler.exitAstNode(node);
        return result;
    }

    @NullMarked
    public interface AstNodeVisitor {
        NodeValue visit(AstMindcodeNode node);
    }
}
