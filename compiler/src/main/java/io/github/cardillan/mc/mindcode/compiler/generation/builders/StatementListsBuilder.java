package io.github.cardillan.mc.mindcode.compiler.generation.builders;

import io.github.cardillan.mc.generated.ast.visitors.*;
import io.github.cardillan.mc.messages.ERR;
import io.github.cardillan.mc.mindcode.compiler.ast.nodes.*;
import io.github.cardillan.mc.mindcode.compiler.astcontext.AstSubcontextType;
import io.github.cardillan.mc.mindcode.compiler.generation.AbstractCodeBuilder;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeGenerator;
import io.github.cardillan.mc.mindcode.compiler.generation.CodeGeneratorContext;
import io.github.cardillan.mc.mindcode.compiler.generation.LoopStack.LoopLabels;
import io.github.cardillan.mc.mindcode.compiler.generation.variables.ValueStore;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicLabel;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicNumber;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicVoid;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorType;
import io.github.cardillan.mc.mindcode.logic.opcodes.ProcessorVersion;
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
        if (!processor.getProcessorVersion().atLeast(ProcessorVersion.V8B)) {
            error(node, ERR.ATOMIC_REQUIRES_TARGET_81);
        }
        if (processor.getProcessorType() == ProcessorType.NO_PROCESSOR) {
            error(node, ERR.ATOMIC_REQUIRES_PROCESSOR);
        }

        final LogicLabel breakLabel = assembler.nextLabel();
        LoopLabels loopLabels =  variables.getLoopStack().enterBlock(node.getLabel(), breakLabel, "atomic");

        assembler.setSubcontextType(AstSubcontextType.BODY, 10.0);
        if (!enterAtomicBlock()) {
            assembler.createWait(LogicNumber.ZERO).setAtomicWait(true);
        }
        ValueStore result = evaluateBody(node.getExpressions());
        exitAtomicBlock();
        assembler.createLabel(breakLabel);
        assembler.clearSubcontextType();
        variables.getLoopStack().exitLoop(loopLabels);

        return node.isFunction() ? result : LogicVoid.VOID;
    }

    @Override
    public ValueStore visitCodeBlock(AstCodeBlock node) {
        boolean active = assembler.isActive();
        if (node.isDebug() && !node.getProfile().isDebug()) {
            assembler.setActive(false);
        }
        final LogicLabel breakLabel = assembler.nextLabel();
        LoopLabels loopLabels =  variables.getLoopStack().enterBlock(node.getLabel(), breakLabel,
                node.isDebug() ? "debug" : "begin");

        assembler.setSubcontextType(AstSubcontextType.BODY, 1.0);
        ValueStore result = evaluateBody(node.getExpressions());
        assembler.createLabel(breakLabel);
        assembler.clearSubcontextType();
        variables.getLoopStack().exitLoop(loopLabels);
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
