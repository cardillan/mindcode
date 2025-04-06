package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public class MindustryInstructionProcessor8 extends BaseInstructionProcessor {

    MindustryInstructionProcessor8(InstructionProcessorParameters parameters) {
        super(parameters);
    }

    @Override
    public boolean isValidIntegerLiteral(long value) {
        // We always encode negative literals as decimal ones, so we avoid the bug.
        return true;
    }

    @Override
    protected Optional<String> mlogFormat(SourcePosition sourcePosition, double value, String literal, boolean allowPrecisionLoss) {
        if (!Double.isFinite(value)) {
            return Optional.empty();
        }
        Optional<String> result = mlogFormatWithoutExponent(value, literal);
        if (result.isPresent()) {
            return result.get().isEmpty() ? Optional.empty() : result;
        } else {
            return mlogFormatWithExponent(sourcePosition, value, Double.toString(value), literal,
                    false, allowPrecisionLoss);
        }
    }

    @Override
    public String formatNumber(double value) {
        // Code taken from Mindustry to achieve an exact match
        return Math.abs(value - Math.round(value)) < 0.00001 ? String.valueOf(Math.round(value)) : String.valueOf(value);
    }

    @Override
    public double parseNumber(String literal) {
        return Double.parseDouble(literal);
    }
}
