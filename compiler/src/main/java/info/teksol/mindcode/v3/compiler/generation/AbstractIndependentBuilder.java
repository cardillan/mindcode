package info.teksol.mindcode.v3.compiler.generation;

import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRange;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;

import java.util.List;

/// Some builders create a separate stateful class for building code. This is a base class for these
/// stateful classes, delegating methods from the AbstractBuilder.
public abstract class AbstractIndependentBuilder<T extends AstMindcodeNode> {
    private final AbstractBuilder builder;
    protected final T node;

    public AbstractIndependentBuilder(AbstractBuilder builder, T node) {
        this.builder = builder;
        this.node = node;
    }

    public Condition outsideRangeCondition(AstRange range) {
        return builder.outsideRangeCondition(range);
    }

    public Condition insideRangeCondition(AstRange range) {
        return builder.insideRangeCondition(range);
    }

    public LogicLabel nextMarker() {
        return builder.nextMarker();
    }

    public LogicLabel nextLabel() {
        return builder.nextLabel();
    }

    public LogicVariable nextNodeResultTemp() {
        return builder.nextNodeResultTemp();
    }

    public LogicVariable nextTemp() {
        return builder.nextTemp();
    }

    public LogicVariable standaloneTemp() {
        return builder.standaloneTemp();
    }

    public LogicValue temporaryCopy(NodeValue nodeValue, ArgumentType argumentType) {
        return builder.temporaryCopy(nodeValue, argumentType);
    }

    public NodeValue resolveLValue(AstExpression targetNode, NodeValue target) {
        return builder.resolveLValue(targetNode, target);
    }

    public NodeValue resolveLValue(AstExpression targetNode) {
        return builder.resolveLValue(targetNode);
    }

    public NodeValue visitExpressions(List<? extends AstMindcodeNode> expressions) {
        return builder.visitExpressions(expressions);
    }

    public void visitStatements(List<? extends AstMindcodeNode> body) {
        builder.visitStatements(body);
    }

    public void compile(AstMindcodeNode node) {
        builder.compile(node);
    }

    public NodeValue evaluate(AstMindcodeNode node) {
        return builder.evaluate(node);
    }

    public NodeValue visit(AstMindcodeNode node, boolean evaluate) {
        return builder.process(node, evaluate);
    }
}
