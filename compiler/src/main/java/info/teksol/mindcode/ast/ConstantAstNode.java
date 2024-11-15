package info.teksol.mindcode.ast;

import info.teksol.mindcode.InputPosition;
import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicLiteral;

// Base class for nodes that represent constants
public abstract class ConstantAstNode extends BaseAstNode {

    public ConstantAstNode(InputPosition inputPosition) {
        super(inputPosition);
    }

    public abstract String getLiteralValue();

    public abstract double getAsDouble();

    public abstract LogicLiteral toLogicLiteral(InstructionProcessor instructionProcessor) throws NumericLiteral.NoValidMlogRepresentationException;

    public abstract ConstantAstNode withInputPosition(InputPosition inputPosition);
}
