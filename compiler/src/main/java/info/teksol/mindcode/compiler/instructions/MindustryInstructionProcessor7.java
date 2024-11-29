package info.teksol.mindcode.compiler.instructions;

import java.util.Optional;

public class MindustryInstructionProcessor7 extends BaseInstructionProcessor {

    MindustryInstructionProcessor7(InstructionProcessorParameters parameters) {
        super(parameters);
    }

    @Override
    protected Optional<String> mlogFormat(double value, String literal) {
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

        return mlogFormatWithExponent(value, Float.toString((float) value), true);
    }

    @Override
    public String formatNumber(double value) {
        // Code taken from Mindustry to achieve an exact match
        return Math.abs(value - (long) value) < 0.00001 ? String.valueOf((long) value) : String.valueOf(value);
    }
}
