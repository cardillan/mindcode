package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.*;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.NodeValue;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StatementListsBuilder extends AbstractBuilder implements
        AstCodeBlockVisitor<NodeValue>,
        AstModuleVisitor<NodeValue>,
        AstParenthesesVisitor<NodeValue>,
        AstProgramVisitor<NodeValue>,
        AstStatementListVisitor<NodeValue>
{
    public StatementListsBuilder(CodeGeneratorContext context, CodeGenerator.AstNodeVisitor mainNodeVisitor) {
        super(context, mainNodeVisitor);
    }

    @Override
    public NodeValue visitCodeBlock(AstCodeBlock node) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        return visitExpressions(node.getExpressions());
    }

    @Override
    public NodeValue visitModule(AstModule node) {
        visitStatements(node.getStatements());
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitParentheses(AstParentheses node) {
        return visit(node.getExpression());
    }

    @Override
    public NodeValue visitProgram(AstProgram node) {
        visitStatements(node.getModules());
        return LogicVoid.VOID;
    }

    @Override
    public NodeValue visitStatementList(AstStatementList node) {
        return visitExpressions(node.getStatements());
    }
}
