package info.teksol.mc.evaluator;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

public class ExpressionValue implements LogicWritable {
    private final InstructionProcessor instructionProcessor;

    private LogicLiteral literal;

    public ExpressionValue(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }

    @Override
    public void setDoubleValue(double value) {
        if (ExpressionEvaluator.invalid(value)) {
            literal = LogicNull.NULL;
        } else {
            literal = instructionProcessor.createLiteral(SourcePosition.EMPTY, value, false).orElse(null);
        }
    }

    @Override
    public void setLongValue(long value) {
        literal = LogicNumber.create(value);
    }

    @Override
    public void setBooleanValue(boolean value) {
        literal = LogicBoolean.get(value);
    }

    public LogicLiteral getLiteral() {
        return literal;
    }
}
