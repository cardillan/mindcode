package info.teksol.mindcode.compiler.optimization;

import info.teksol.mindcode.compiler.instructions.InstructionProcessor;
import info.teksol.mindcode.logic.LogicBoolean;
import info.teksol.mindcode.logic.LogicLiteral;
import info.teksol.mindcode.logic.LogicNumber;
import info.teksol.mindcode.processor.MindustryResult;

import java.util.Optional;

class ExpressionValue implements MindustryResult {
    private final InstructionProcessor instructionProcessor;

    private LogicLiteral literal;

    public ExpressionValue(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }

    @Override
    public void setDoubleValue(double value) {
        Optional<String> strValue = instructionProcessor.mlogFormat(value);
        literal = strValue.map(s -> LogicNumber.create(s, value)).orElse(null);
    }

    @Override
    public void setLongValue(long value) {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            literal = LogicNumber.get((int) value);
        } else {
            setDoubleValue(value);
        }
    }

    @Override
    public void setBooleanValue(boolean value) {
        literal = LogicBoolean.get(value);
    }

    public LogicLiteral getLiteral() {
        return literal;
    }
}
