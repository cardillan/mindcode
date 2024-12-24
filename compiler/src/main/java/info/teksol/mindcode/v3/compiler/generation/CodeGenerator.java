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
import info.teksol.mindcode.v3.compiler.callgraph.LogicFunctionV3;
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
        nodeVisitor.registerVisitor(new AssignmentsBuilder(this, context));
        nodeVisitor.registerVisitor(new BreakContinueStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new CaseExpressionsBuilder(this, context));
        nodeVisitor.registerVisitor(new DeclarationsBuilder(this, context));
        nodeVisitor.registerVisitor(new ForEachLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new IdentifiersBuilder(this, context));
        nodeVisitor.registerVisitor(new IfExpressionsBuilder(this, context));
        nodeVisitor.registerVisitor(new IteratedForLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new LiteralsBuilder(this, context));
        nodeVisitor.registerVisitor(new OperatorsBuilder(this, context));
        nodeVisitor.registerVisitor(new RangedForLoopStatementsBuilder(this, context));
        nodeVisitor.registerVisitor(new StatementListsBuilder(this, context));
        nodeVisitor.registerVisitor(new WhileLoopStatementsBuilder(this, context));
    }

    public void generateCode(AstProgram program) {
        // TODO In relaxed syntax, top context is the main body
        //      In strict syntax, top context is the root context
        variables.enterFunction(callGraph.getMain(), "", List.of());
        context.setTopAstContext(assembler.getAstContext());

        visit(program, false);

        variables.exitFunction(callGraph.getMain());

        // Check stack allocations
        if (context.stackAllocation() == null) {
            callGraph.recursiveFunctions().filter(LogicFunctionV3::isUsed).forEach(f -> error(f.getDeclaration(),
                    "Function '%s' is recursive and no stack was allocated.", f.getName()));
        }

        // Process function declarations
        appendFunctionDeclarations();
    }

    private void appendFunctionDeclarations() {
        emitEnd();

//        for (LogicFunction function : callGraph.getFunctions()) {
//            if (function.isInline() || !function.isUsed()) {
//                continue;
//            }
//
//            assembler.enterFunctionAstNode(function, function.getDeclaration(), function.getUseCount());
//            currentFunction = function;
//            functionPrefix = function.getPrefix();
//            functionContext = new LogicInstructionGenerator.LocalContext();
//            emit(createLabel(function.getLabel()));
//            final LogicValue returnValue = function.isVoid() ? VOID : LogicVariable.fnRetVal(function);
//            returnStack.enterFunction(nextLabel(), returnValue);
//
//            if (function.isRecursive()) {
//                appendRecursiveFunctionDeclaration(function);
//            } else {
//                appendStacklessFunctionDeclaration(function);
//            }
//
//            emitEnd();
//            exitAstNode(function.getDeclaration());
//
//            functionPrefix = "";
//            returnStack.exitFunction();
//            currentFunction = callGraph.getMain();
//        }
    }

    private void emitEnd() {
        assembler.setSubcontextType(AstSubcontextType.END, 1.0);
        assembler.createEnd();
        assembler.clearSubcontextType();
    }

    public NodeValue visit(AstMindcodeNode node, boolean evaluate) {
        assembler.enterAstNode(node);
        variables.enterAstNode();
        NodeValue result = nodeVisitor.visit(evaluator.evaluate(node));
        variables.exitAstNode();
        assembler.exitAstNode(node);
        return result;
    }

    @NullMarked
    public interface AstNodeVisitor {
        NodeValue visit(AstMindcodeNode node, boolean evaluate);
    }
}
