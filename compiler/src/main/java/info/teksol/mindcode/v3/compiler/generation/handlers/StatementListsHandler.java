package info.teksol.mindcode.v3.compiler.generation.handlers;

import info.teksol.generated.ast.visitors.*;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.AstNodeHandler;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StatementListsHandler extends BaseHandler implements
        AstCodeBlockVisitor<NodeValue>,
        AstModuleVisitor<NodeValue>,
        AstParenthesesVisitor<NodeValue>,
        AstProgramVisitor<NodeValue>,
        AstStatementListVisitor<NodeValue>
{
    public StatementListsHandler(CodeGeneratorContext context, AstNodeHandler mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitCodeBlock(AstCodeBlock node) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        return node.getExpressions().stream().map(this::visit).reduce(LogicVoid.VOID, (a, b) -> b);
    }

    @Override
    public NodeValue visitModule(AstModule node) {
        node.getStatements().forEach(this::visit);
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitParentheses(AstParentheses node) {
        return visit(node.getExpression());
    }

    @Override
    public NodeValue visitProgram(AstProgram node) {
        node.getModules().forEach(this::visitModule);
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitStatementList(AstStatementList node) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        return node.getStatements().stream().map(this::visit).reduce(LogicVoid.VOID, (a, b) -> b);
    }
}
