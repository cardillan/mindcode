package info.teksol.mc.mindcode.compiler.generation.builders;

import info.teksol.mc.generated.ast.visitors.*;
import info.teksol.mc.messages.ERR;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.compiler.astcontext.AstSubcontextType;
import info.teksol.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import info.teksol.mc.mindcode.compiler.generation.CodeGenerator;
import info.teksol.mc.mindcode.compiler.generation.CodeGeneratorContext;
import info.teksol.mc.mindcode.compiler.generation.variables.ValueStore;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.arguments.LogicVoid;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StatementListsBuilder extends AbstractCodeBuilder implements
        AstAtomicBlockVisitor<ValueStore>,
        AstCodeBlockVisitor<ValueStore>,
        AstModuleVisitor<ValueStore>,
        AstParenthesesVisitor<ValueStore>,
        AstProgramVisitor<ValueStore>,
        AstStatementListVisitor<ValueStore>
{
    // The atomic section is active
    private boolean atomic;

    public StatementListsBuilder(CodeGenerator codeGenerator, CodeGeneratorContext context) {
        super(codeGenerator, context);
    }

    @Override
    public ValueStore visitAtomicBlock(AstAtomicBlock node) {
        assembler.setSubcontextType(AstSubcontextType.BODY, 10.0);
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8B)) {
            error(node, ERR.ATOMIC_REQUIRES_TARGET_81);
        }
        ValueStore result;
        if (atomic) {
            error(node, ERR.ATOMIC_BLOCK_NESTED);
            result = evaluateBody(node.getExpressions());
        } else {
            atomic = true;
            assembler.createWait(LogicNumber.ZERO);
            result = evaluateBody(node.getExpressions());
            atomic = false;
        }
        assembler.clearSubcontextType();
        return result;
    }

    @Override
    public ValueStore visitCodeBlock(AstCodeBlock node) {
        boolean active = assembler.isActive();
        if (node.isDebug() && !node.getProfile().isDebug()) {
            assembler.setActive(false);
        }
        assembler.setSubcontextType(AstSubcontextType.BODY, 1.0);
        ValueStore result = evaluateBody(node.getExpressions());
        assembler.clearSubcontextType();
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
