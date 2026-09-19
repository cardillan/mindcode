package info.teksol.mc.mindcode.logic.instructions;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.arguments.LogicArgument;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.util.UtfUtils;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Optional;

@NullMarked
public class MindustryInstructionProcessor82 extends MindustryInstructionProcessor8 {

    MindustryInstructionProcessor82(InstructionProcessorParameters parameters) {
        super(parameters);
    }

    @Override
    public boolean canEncode(int character) {
        return UtfUtils.canEncode(character);
    }

    @Override
    public String toMlog(LogicArgument argument) {
        return argument.toMlog();
    }

    @Override
    protected LogicNumber createLogicNumber(SourcePosition sourcePosition, String literal, double value) {
        return LogicNumber.create(sourcePosition, literal, value);
    }

    public boolean isValidHexLiteral(long value) {
        return true;
    }

    protected Optional<String> mlogFormatWithoutExponent(double value, String literal) {
        double absoluteValue = Math.abs(value);
        if (absoluteValue == 0.0) {
            return Optional.of("0");
        } else if (1e-18 <= absoluteValue && absoluteValue <= Long.MAX_VALUE) {
            long longValue = (long) value;
            if (longValue == value) {
                // All longs can be encoded
                return Optional.of(longValue == Long.MIN_VALUE ? "0xFFFFFFFFFFFFFFFF" : Long.toString(longValue));
            }

            BigDecimal decimal = new BigDecimal(literal, CONVERSION_CONTEXT);
            if (decimal.compareTo(LONG_MAX) > 0) return Optional.empty();
            String result = decimal.stripTrailingZeros().toPlainString();
            // At more than 18 digits, we're potentially losing precision: prefer the exponential form
            return result.length() > 18 ? Optional.empty() : Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    protected Optional<String> mlogFormatWithExponent(SourcePosition sourcePosition, double value, String literal,
            String originalLiteral, boolean floatPrecision, boolean allowPrecisionLoss) {
        // Mindustry v160 can parse exponential form
        return Optional.of(literal);
    }
}
