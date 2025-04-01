package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public class MindustryInstructionProcessor7 extends BaseInstructionProcessor {

    MindustryInstructionProcessor7(InstructionProcessorParameters parameters) {
        super(parameters);
    }

    @Override
    public boolean isValidIntegerLiteral(long value) {
        return value != Integer.MIN_VALUE;
    }

    @Override
    protected Optional<String> mlogFormat(SourcePosition sourcePosition, double value, String literal, boolean allowPrecisionLoss) {
        if (!Double.isFinite(value)) {
            return Optional.empty();
        }

        Optional<String> result = mlogFormatWithoutExponent(value, literal);
        if (result.isPresent()) {
            return result.get().isEmpty() ? Optional.empty() : result;
        }

        // Can it be represented as a float at all?
        double abs = Math.abs(value);
        if ((float) abs == 0f || !Float.isFinite((float) abs)) {
            return Optional.empty();
        }

        return mlogFormatWithExponent(sourcePosition, value, Float.toString((float) value), literal,
                true, allowPrecisionLoss);
    }

    @Override
    public String formatNumber(double value) {
        // Code taken from Mindustry to achieve an exact match
        return Math.abs(value - (long) value) < 0.00001 ? String.valueOf((long) value) : String.valueOf(value);
    }

    @Override
    public double parseNumber(String literal) {
        return literal.indexOf('e') >= 0 || literal.indexOf('E') >= 0
                ? Float.parseFloat(literal)
                : Double.parseDouble(literal);
    }
}
