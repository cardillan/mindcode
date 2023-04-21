package info.teksol.mindcode.ast;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;

// Base class for nodes that represent constants
public abstract class ConstantAstNode extends BaseAstNode {

    protected ConstantAstNode() {
    }

    public abstract double getAsDouble();

    public abstract LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor);
}
