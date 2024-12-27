package info.teksol.mindcode.v3.compiler.generation.builders;

import info.teksol.generated.ast.visitors.*;
import info.teksol.mindcode.logic.LogicVoid;
import info.teksol.mindcode.v3.compiler.ast.nodes.*;
import info.teksol.mindcode.v3.compiler.generation.AbstractBuilder;
import info.teksol.mindcode.v3.compiler.generation.CodeGenerator;
import info.teksol.mindcode.v3.compiler.generation.CodeGeneratorContext;
import info.teksol.mindcode.v3.compiler.generation.variables.ValueStore;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StatementListsBuilder extends AbstractBuilder implements
        AstCodeBlockVisitor<ValueStore>,
        AstModuleVisitor<ValueStore>,
        AstParenthesesVisitor<ValueStore>,
        AstProgramVisitor<ValueStore>,
        AstStatementListVisitor<ValueStore>
{
    public StatementListsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitCodeBlock(AstCodeBlock node) {
        // The accumulator ensures we'll evaluate all nodes and return the last node evaluation as the result
        return evaluateBody(node.getExpressions());
    }

    @Override
    public ValueStore visitModule(AstModule node) {
        visitBody(node.getStatements());
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitParentheses(AstParentheses node) {
        return evaluate(node.getExpression());
    }

    @Override
    public ValueStore visitProgram(AstProgram node) {
        visitBody(node.getModules());
        return LogicVoid.VOID;
    }

    @Override
    public ValueStore visitStatementList(AstStatementList node) {
        return evaluateBody(node.getStatements());
    }
}
