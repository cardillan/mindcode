package info.teksol.mc.evaluator;

import info.teksol.mc.mindcode.logic.arguments.LogicBoolean;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicNull;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;

import java.util.Optional;

// TODO add support for conversion to AST literals
public class ExpressionValue implements LogicWritable {
    private final InstructionProcessor instructionProcessor;

    private LogicLiteral literal;

    public ExpressionValue(InstructionProcessor instructionProcessor) {
        this.instructionProcessor = instructionProcessor;
    }

    @Override
    public void setDoubleValue(double value) {
        if (invalid(value)) {
            literal = LogicNull.NULL;
        } else {
            Optional<String> strValue = instructionProcessor.mlogFormat(value);
            literal = strValue.map(s -> LogicNumber.get(s, value)).orElse(null);
        }
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

    public static boolean invalid(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }
}
