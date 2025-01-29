package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.messages.AbstractMessageEmitter;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstExpression;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstIdentifier;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstRange;
import info.teksol.mc.mindcode.compiler.callgraph.CallGraph;
import info.teksol.mc.mindcode.compiler.callgraph.MindcodeFunction;
import info.teksol.mc.mindcode.compiler.evaluator.CompileTimeEvaluator;
import info.teksol.mc.mindcode.compiler.generation.variables.FunctionArgument;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.compiler.generation.variables.Variables;
import info.teksol.mc.mindcode.logic.arguments.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.profile.CompilerProfile;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Supplier;

/// Base class for code builders. Each builder generates code for a subset os AST node types.
/// Instances of these classes need to be registered in CodeGenerator, and no two classes may handle the
/// same AST Node type. No checks are made to enforce this constraint. Unhandled node types cause errors
/// when encountered.
///
/// Individual builders implement a specific interface(s) from the `SingleAstNodeVisitor<ValueStore>`
/// class hierarchy. When registered, builders are automatically assigned to handle AST nodes corresponding
/// to the interfaces they implement. Each builder returns a ValueStore instance representing the value
/// of the node. Nodes not having any value should be evaluated to `LogicVoid.VOID`.
///
/// This class provides some methods that are useful to more than one builder.
/// Foremost, there's a `visit(AstMindcodeNode node)` method, which is used to evaluate a child node using
/// all registered builders (potentially including the current one).
@NullMarked
public abstract class AbstractBuilder extends AbstractMessageEmitter {
    private final CodeGenerator codeGenerator;

    protected final CodeGeneratorContext context;
    protected final CompilerProfile profile;
    protected final InstructionProcessor processor;
    protected final CallGraph callGraph;
    protected final CodeAssembler assembler;
    protected final Variables variables;
    protected final ReturnStack returnStack;
    protected final CompileTimeEvaluator evaluator;

    protected AbstractBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(context.messageConsumer());
        this.codeGenerator = codeGenerator;
        this.context = context;

        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        callGraph = context.callGraph();
        assembler = context.assembler();
        variables = context.variables();
        returnStack = context.returnStack();
        evaluator = context.compileTimeEvaluator();
    }

    protected AbstractBuilder(AbstractBuilder builder) {
        super(builder.context.messageConsumer());
        this.codeGenerator = builder.codeGenerator;
        this.context = builder.context;

        profile = context.compilerProfile();
        processor = context.instructionProcessor();
        callGraph = context.callGraph();
        assembler = context.assembler();
        variables = context.variables();
        returnStack = context.returnStack();
        evaluator = context.compileTimeEvaluator();
    }

    protected LogicValue resolveTarget(AstMindcodeNode node, @PrintFormat String message, Object... args) {
        ValueStore valueStore = process(node, false);
        return switch(valueStore) {
            case LogicVariable var    -> var;
            case LogicBuiltIn builtIn -> builtIn;
            default -> {
                error(node, message, args);
                yield LogicVariable.INVALID;
            }
        };
    }

    protected SourcePosition pos(SourceElement start, SourceElement end) {
        return start.sourcePosition().upTo(end.sourcePosition());
    }

    protected SourcePosition pos(List<? extends SourceElement> elements) {
        return elements.getFirst().sourcePosition().upTo(elements.getLast().sourcePosition());
    }

    protected boolean isLocalContext() {
        return codeGenerator.isLocalContext();
    }

    protected void enterFunction(MindcodeFunction function, List<FunctionArgument> varargs) {
        codeGenerator.enterFunction(function, varargs);
    }

    protected void exitFunction(MindcodeFunction function) {
        codeGenerator.exitFunction(function);
    }

    protected boolean allowUndeclaredLinks() {
        return codeGenerator.allowUndeclaredLinks();
    }

    protected ValueStore processInLocalScope(Supplier<ValueStore> process) {
        return codeGenerator.processInLocalScope(process);
    }

    /// Processes the node by passing it to the proper builder according to node type. The builder creates
    /// code from the AST node and provides a `ValueStore` instance representing the output value of the node.
    ///
    /// @param node node to process
    /// @param evaluate when `true`, the caller is interested in the result produced by the node
    /// @return a `ValueStore` instance representing the value of the node. When `evaluate` is `false`, the returned
    ///         instance might not represent the actual value of the node.
    protected ValueStore process(AstMindcodeNode node, boolean evaluate) {
        return codeGenerator.visit(node, evaluate);
    }

    /// Evaluates the node by passing it to the proper builder according to node type. The builder creates
    /// code from the AST node and provides a `ValueStore` instance representing the output value of the node.
    ///
    /// @param node node to process
    /// @return a `ValueStore` instance representing the value of the node

    protected ValueStore evaluate(AstMindcodeNode node) {
        return codeGenerator.visit(node, true);
    }

    /// Processes the node by passing it to the proper builder according to node type, ignoring the resulting value
    /// of the node. To be called when the resulting value of the node is not needed for further processing
    /// (e.g. expressions but the last one in a statement list, init or update clause of the for statement, or
    /// the function body of a `void` function).
    ///
    /// @param node node to process
    protected void compile(AstMindcodeNode node) {
        codeGenerator.visit(node, false);
    }

    /// Visits a body of statements, disregarding the resulting values of all nodes.
    ///
    /// @return `LogicVoid.VOID`
    protected ValueStore visitBody(List<? extends AstMindcodeNode> body) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        body.forEach(this::compile);
        return LogicVoid.VOID;
    }

    ///  Evaluates a body of statements/expressions, returning the `ValueStore` of the last expression
    /// (which represents the resulting value of the entire body).
    ///
    /// @return the value of the last expression in the list
    protected ValueStore evaluateBody(List<? extends AstMindcodeNode> expressions) {
        if (expressions.isEmpty()) {
            return LogicVoid.VOID;
        }

        for (int i = 0; i < expressions.size() - 1; i++) {
            compile(expressions.get(i));
        }
        return evaluate(expressions.getLast());
    }

    ///  Evaluates every expression in the list, returning a list of evaluated values.
    ///
    /// @return the value of all expressions in list
    protected List<ValueStore> evaluateExpressions(List<? extends AstMindcodeNode> expressions) {
        return expressions.stream().map(this::evaluate).toList();
    }

    /// Evaluates every expression in the list, returning a list of evaluated values. Any compile-time evaluations
    /// made while evaluating the parts are cleared up upon return. Used to evaluate FormattableContent,
    /// because it can be evaluated in different contexts and caching compile-time evaluations might lead to
    /// wrong evaluation next time.
    ///
    /// @return the value of all expressions in list
    protected List<ValueStore> evaluateExpressionsUncached(List<? extends AstMindcodeNode> expressions) {
        List<ValueStore> list = expressions.stream().map(this::evaluate).toList();
        evaluator.purgeFromCache(expressions);
        return list;
    }

    /// Tries to resolve given expression as an l-value. Reports an appropriate error when the expression
    /// doesn't represent an l-value.
    ///
    /// When the expression doesn't represent an l-value, `LogicVariable.INVALID` is returned. This outcome
    /// can be tested by comparing the returned value to `LogicVariable.INVALID`, or, preferably, by calling
    /// `isLValue()` on the returned instance, which will return `false`.
    ///
    /// @return a ValueStore representing the l-value, or `LogicVariable.INVALID`.
    protected ValueStore resolveLValue(AstExpression targetNode) {
        return resolveLValue(targetNode, evaluate(targetNode));
    }

    /// Tries to resolve an expression, which vas already processed, as an l-value. To be used when the node value
    /// needs to be read first (and therefore the node needs to be evaluated), and then a value needs to be written
    /// - for example an in out function argument.
    protected ValueStore resolveLValue(AstExpression targetNode, ValueStore target) {
        if (target.isLvalue()) {
            return target;
        } else {
            // We're trying to report the error as precisely as possible
            if (targetNode instanceof AstIdentifier identifier) {
                // We got a read-only identifier. It can be either a constant, or a parameter
                error(targetNode, ERR.LVALUE_ASSIGNMENT_TO_CONST_NOT_ALLOWED, identifier.getName());
            } else {
                switch (target) {
                    case LogicVariable v -> reportVariableError(targetNode, v);
                    case LogicLiteral l -> error(targetNode, ERR.LVALUE_VARIABLE_EXPECTED);
                    default -> error(targetNode, ERR.LVALUE_CANNOT_ASSIGN_TO_EXPRESSION);
                }
            }
            return LogicVariable.INVALID;
        }
    }

    private void reportVariableError(AstExpression targetNode, LogicVariable variable) {
        switch (variable.getType()) {
            case BLOCK      -> error(targetNode, ERR.LVALUE_ASSIGNMENT_TO_LINKED_NOT_ALLOWED, variable.getName());
            case PARAMETER  -> error(targetNode, ERR.LVALUE_ASSIGNMENT_TO_PARAM_NOT_ALLOWED);
            default         -> error(targetNode, ERR.LVALUE_CANNOT_ASSIGN_TO_EXPRESSION);
        }
    }

    protected Condition insideRangeCondition(AstRange range) {
        return range.isExclusive() ? Condition.LESS_THAN : Condition.LESS_THAN_EQ;
    }

    protected Condition outsideRangeCondition(AstRange range) {
        return insideRangeCondition(range).inverse();
    }
}
