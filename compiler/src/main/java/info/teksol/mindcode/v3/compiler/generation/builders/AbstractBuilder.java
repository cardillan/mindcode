package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.*;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstExpression;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstIdentifier;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mindcode.v3.compiler.ast.nodes.AstRange;
import info.teksol.mindcode.v3.compiler.callgraph.CallGraph;
import info.teksol.mindcode.v3.compiler.generation.CodeBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import info.teksol.mindcode.v3.compiler.generation.variables.Variables;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// Base class for code builders. Each builder generates code for a subset os AST node types.
/// Instances of these classes need to be registered in CodeGenerator, and no two classes may handle the
/// same AST Node type. No checks are made to enforce this constraint. Unhandled node types cause errors
/// when encountered.
///
/// Individual builders implement a specific interface(s) from the `SingleAstNodeVisitor<NodeValue>`
/// class hierarchy. When registered, builders are automatically assigned to handle AST nodes corresponding
/// to the interfaces they implement. Each builder returns a NodeValue instance representing the value
/// of the node. Nodes not having any value should be evaluated to `LogicVoid.VOID`.
///
/// This class provides some methods that are useful to more than one builder.
/// Foremost, there's a `visit(AstMindcodeNode node)` method, which is used to evaluate a child node using
/// all registered builders (potentially including the current one).
@NullMarked
abstract class AbstractBuilder extends AbstractMessageEmitter {
    /// The node visitor dispatcher
    private final CodeGenerator.AstNodeVisitor mainNodeVisitor;

    protected final CodeGeneratorContext context;
    protected final InstructionProcessor processor;
    protected final CallGraph callGraph;
    protected final CodeBuilder codeBuilder;
    protected final Variables variables;

    protected AbstractBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context.messageConsumer());
        this.mainNodeVisitor = mainNodeVisitor;
        this.context = context;

        processor = context.instructionProcessor();
        callGraph = context.callGraph();
        codeBuilder = context.codeBuilder();
        variables = context.variables();
    }

    /// Evaluates the node using the main visitor.
    protected NodeValue visit(AstMindcodeNode node) {
        return mainNodeVisitor.visit(node);
    }

    /// Visits a body of statements, disregarding the resulting values.
    protected void visitStatements(List<? extends AstMindcodeNode> body) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        body.forEach(this::visit);
    }

    ///  Evaluates a body of statements/expressions, returning the NodeValue of the last expression
    /// (which represents the resulting value of the entire body).
    protected NodeValue visitExpressions(List<? extends AstMindcodeNode> expressions) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        return expressions.stream().map(this::visit).reduce(LogicVoid.VOID, (a, b) -> b);
    }

    /// Some handlers create a separate stateful class for building code. This is a base class for these
    /// stateful classes, providing the `visit` method directly for better code compatibility.
    protected abstract class StructureBuilder<T extends AstMindcodeNode> {
        protected final T node;

        public StructureBuilder(T node) {
            this.node = node;
        }

        protected NodeValue visit(AstMindcodeNode node) {
            return mainNodeVisitor.visit(node);
        }
    }

    /// Tries to resolve given expression as an l-value. Reports an appropriate error when the expression
    /// doesn't represent an l-value.
    ///
    /// When the expression doesn't represent an l-value, `LogicVariable.INVALID` is returned. This outcome
    /// can be tested by comparing the returned value to `LogicVariable.INVALID`, or, preferably, by calling
    /// `isLValue()` on the returned instance, which will return `false`.
    ///
    /// @return a NodeValue representing the l-value, or `LogicVariable.INVALID`.
    protected NodeValue resolveLValue(AstExpression targetNode) {
        return resolveLValue(targetNode, visit(targetNode));
    }

    protected NodeValue resolveLValue(AstExpression targetNode, NodeValue target) {
        if (target.isLvalue()) {
            return target;
        } else {
            // We're trying to report the error as precisely as possible
            if (targetNode instanceof AstIdentifier identifier) {
                // We got a read-only identifier. It can be either a constant, or a parameter
                error(targetNode, "Assignment to constant or parameter '%s' not allowed.", identifier.getName());
            } else {
                switch (target) {
                    case LogicVariable v -> reportVariableError(targetNode, v);
                    case LogicLiteral l -> error(targetNode, "Variable expected.");
                    default -> error(targetNode, "Cannot assign a value to this expression.");
                }
            }
            return LogicVariable.INVALID;
        }
    }

    private void reportVariableError(AstExpression targetNode, LogicVariable variable) {
        switch (variable.getType()) {
            case BLOCK      -> error(targetNode, "Assignment to variable '%s' representing a linked block not allowed.", variable.getName());
            case PARAMETER  -> error(targetNode, "Assignment to a parameter not allowed.");
            default         -> error(targetNode, "Cannot assign a value to this expression.");
        }
    }

    /// Stores the current value of nodeValue to a temp variable. If the node value is a literal,
    /// returns the literal directly, as it cannot be changed.
    ///
    /// @param nodeValue the value to protect
    /// @return an independent copy of the current value of the node
    protected LogicValue temporaryCopy(NodeValue nodeValue, ArgumentType argumentType) {
        if (nodeValue instanceof LogicValue value && value.isConstant()) {
            return value;
        } else {
            LogicVariable tmp = processor.nextTemp().withType(argumentType);
            variables.registerNodeVariable(tmp);
            codeBuilder.createSet(tmp, nodeValue.getValue(codeBuilder));
            return tmp;
        }
    }

    /// Allocates a new temporary variable whose lifespan doesn't enter another node (child, sibling or parent).
    /// The variable will **NOT** be pushed on the stack for recursive calls.
    ///
    /// @return a temporary variable for use strictly within the current AST node
    protected LogicVariable standaloneTemp() {
        return processor.nextTemp();
    }


    /// Allocates a new temporary variable whose scope is limited to a node (i.e. not needed outside that node).
    /// The variable will be pushed on the stack if needed.
    ///
    /// @return a temporary variable for use within and below the current AST node
    protected LogicVariable nextTemp() {
        LogicVariable variable = processor.nextTemp();
        variables.registerNodeVariable(variable);
        return variable;
    }

    /// Allocates a new temporary variable which transfers a value from child to parent node.
    /// The variable will be pushed on the stack if needed.
    ///
    /// @return a temporary variable for use within the parent of the current AST node
    protected LogicVariable nextNodeResultTemp() {
        LogicVariable variable = processor.nextTemp();
        variables.registerParentNodeVariable(variable);
        return variable;
    }

    public LogicLabel nextLabel() {
        return processor.nextLabel();
    }

    public LogicLabel nextMarker() {
        return processor.nextMarker();
    }

    protected Condition insideRangeCondition(AstRange range) {
        return range.isExclusive() ? Condition.LESS_THAN : Condition.LESS_THAN_EQ;
    }

    protected Condition outsideRangeCondition(AstRange range) {
        return insideRangeCondition(range).inverse();
    }
}
