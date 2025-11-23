package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StatementListsBuilder extends AbstractCodeBuilder implements
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
        boolean active = assembler.isActive();
        if (node.isDebug() && !node.getProfile().isDebug()) {
            assembler.setActive(false);
        }
        ValueStore result = evaluateBody(node.getExpressions());
        assembler.setActive(active);
        return result;
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
