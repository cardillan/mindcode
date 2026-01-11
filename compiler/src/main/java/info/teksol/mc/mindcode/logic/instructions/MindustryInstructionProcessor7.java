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
    public boolean isValidHexLiteral(long value) {
        return value >= 0;
    }

    @Override
    public boolean isValidIntegerLiteral(long value) {
        return value != Long.MIN_VALUE && value != Integer.MIN_VALUE;
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
        float f = (float) value;
        if (f == 0f || !Float.isFinite(f)) {
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
}
