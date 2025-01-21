package info.teksol.mc.evaluator;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

import java.util.Optional;

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
            Optional<String> strValue = instructionProcessor.mlogFormat(SourcePosition.EMPTY, value, false);
            literal = strValue.map(s -> LogicNumber.create(instructionProcessor, s)).orElse(null);
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
