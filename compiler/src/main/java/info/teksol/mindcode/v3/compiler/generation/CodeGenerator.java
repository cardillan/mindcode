package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.generated.ast.ComposedAstNodeVisitor;
import info.teksol.mindcode.MindcodeInternalError;
import info.teksol.mindcode.compiler.functions.FunctionMapper;
import info.teksol.mindcode.compiler.functions.FunctionMapperFactory;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstProgram;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunction;
import info.teksol.mindcode.v3.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mindcode.v3.compiler.generation.handlers.*;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import info.teksol.mindcode.v3.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CodeGenerator extends AbstractMessageEmitter {
    // Stateless processing instances
    private final CodeGeneratorContext context;
    private final CallGraph callGraph;
    private final CompileTimeEvaluator evaluator;
    private final CodeBuilder codeBuilder;
    private final Variables variables;

    private final FunctionMapper functionMapper;
    private final ComposedAstNodeVisitor<NodeValue> nodeVisitor;

    // Stateful instances

    public CodeGenerator(CodeGeneratorContext context) {
        super(context.messageConsumer());
        this.context = context;
        callGraph = context.callGraph();
        evaluator = context.compileTimeEvaluator();
        codeBuilder = context.codeBuilder();
        variables = context.variables();

        functionMapper = FunctionMapperFactory.getFunctionMapper(context.instructionProcessor(),
                codeBuilder::getAstContext, messageConsumer);

        nodeVisitor = new ComposedAstNodeVisitor<>(node -> { throw new MindcodeInternalError("Unhandled node " + node); });

        // Registration order is unimportant as long as multiple handlers do not handle the same node
        nodeVisitor.registerVisitor(new StatementListsHandler(context, this::visit));
        nodeVisitor.registerVisitor(new DeclarationsHandler(context, this::visit));
        nodeVisitor.registerVisitor(new IdentifiersHandler(context, this::visit));
        nodeVisitor.registerVisitor(new LiteralsHandler(context, this::visit));
        nodeVisitor.registerVisitor(new AssignmentsHandler(context, this::visit));
        nodeVisitor.registerVisitor(new OperatorsHandler(context, this::visit));
    }

    public void generateCode(AstProgram program) {
        // TODO In relaxed syntax, top context is the main body
        //      In strict syntax, top context is the root context
        variables.enterFunction(callGraph.getMain(), "");
        context.setTopAstContext(codeBuilder.getAstContext());

        visit(program);

        variables.exitFunction(callGraph.getMain());

        // Check stack allocations
        if (context.stackAllocation() == null) {
            callGraph.recursiveFunctions().filter(LogicFunction::isUsed).forEach(f -> error(f.getDeclaration(),
                    "Function '%s' is recursive and no stack was allocated.", f.getName()));
        }

        // Process function declarations
    }

    private NodeValue visit(AstMindcodeNode node) {
        codeBuilder.enterAstNode(node);
        variables.enterAstNode();
        NodeValue result = nodeVisitor.visit(evaluator.evaluate(node));
        variables.exitAstNode();
        codeBuilder.exitAstNode(node);
        return result;
    }
}
