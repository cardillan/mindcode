package io.github.cardillan.mc.evaluator;

import io.github.cardillan.mc.common.SourcePosition;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicBoolean;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicLiteral;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicNull;
import io.github.cardillan.mc.mindcode.logic.arguments.LogicNumber;
import io.github.cardillan.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ExpressionValue implements LogicWritable {
    private final InstructionProcessor instructionProcessor;

    private @Nullable LogicLiteral literal;

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
        literal = instructionProcessor.isValidIntegerLiteral(value) ? LogicNumber.create(value) : null;
    }

    @Override
    public void setBooleanValue(boolean value) {
        literal = LogicBoolean.get(value);
    }

    public @Nullable LogicLiteral getLiteral(SourcePosition position) {
        return literal == null ? null : literal.withSourcePosition(position);
    }
}
