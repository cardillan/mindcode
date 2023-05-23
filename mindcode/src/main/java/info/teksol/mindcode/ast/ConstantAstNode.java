package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;
import org.antlr.v4.runtime.Token;

// Base class for nodes that represent constants
public abstract class ConstantAstNode extends BaseAstNode {

    public ConstantAstNode(Token startToken) {
        super(startToken);
    }

    public abstract double getAsDouble();

    public abstract LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor);
}
