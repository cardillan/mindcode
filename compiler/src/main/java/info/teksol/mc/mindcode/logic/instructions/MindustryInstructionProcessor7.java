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
    protected Optional<String> mlogFormat(SourcePosition sourcePosition, double value, String literal, boolean allowPrecisionLoss) {
        if (!Double.isFinite(value)) {
            return Optional.empty();
        }

        double abs = Math.abs(value);
        Optional<String> result = mlogFormatWithoutExponent(abs, literal);
        if (result.isPresent()) {
            return result;
        }

        // Can it be represented as a float at all?
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
