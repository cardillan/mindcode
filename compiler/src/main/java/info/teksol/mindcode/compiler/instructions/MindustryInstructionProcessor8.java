package info.teksol.mindcode.compiler.instructions;

import java.util.Optional;

public class MindustryInstructionProcessor8 extends BaseInstructionProcessor {

    MindustryInstructionProcessor8(InstructionProcessorParameters parameters) {
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
        } else {
            return mlogFormatWithExponent(value, Double.toString(value), false);
        }
    }

    @Override
    public String formatNumber(double value) {
        // Code taken from Mindustry to achieve an exact match
        return Math.abs(value - Math.round(value)) < 0.00001 ? String.valueOf(Math.round(value)) : String.valueOf(value);
    }
}
