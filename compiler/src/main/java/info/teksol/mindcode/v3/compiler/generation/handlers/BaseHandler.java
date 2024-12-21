package info.teksol.mindcode.v3.compiler.generation.handlers;

import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicVariable;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.generation.AstNodeHandler;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import info.teksol.mindcode.v3.compiler.generation.variables.Variables;

abstract class BaseHandler extends AbstractMessageEmitter {
    /// The node visitor dispatcher
    private final AstNodeHandler mainNodeVisitor;

    protected final CodeGeneratorContext context;
    protected final InstructionProcessor processor;
    protected final CallGraph callGraph;
    protected final CodeBuilder codeBuilder;
    protected final Variables variables;

    public BaseHandler(CodeGeneratorContext context, AstNodeHandler mainNodeVisitor) {
        super(context.messageConsumer());
        this.mainNodeVisitor = mainNodeVisitor;
        this.context = context;

        processor = context.instructionProcessor();
        callGraph = context.callGraph();
        codeBuilder = context.codeBuilder();
        variables = context.variables();
    }

    public NodeValue visit(AstMindcodeNode node) {
        return mainNodeVisitor.visit(node);
    }

    // Allocates a new temporary variable which holds the evaluated value of a node
    protected LogicVariable nextNodeResultTemp() {
        LogicVariable variable = processor.nextTemp();
        variables.registerParentNodeVariable(variable);
        return variable;
    }

}
