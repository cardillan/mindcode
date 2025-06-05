package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.arguments.LogicColor;
import info.teksol.mc.mindcode.logic.arguments.LogicLiteral;
import info.teksol.mc.mindcode.logic.arguments.LogicNumber;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/// IntermediateValue holds an intermediate result of compile-time expression evaluation.
/// The intermediate result might not have an mlog representation, and as such cannot be stored
/// in `AstLiteralFloat`.
@NullMarked
public class IntermediateValue extends AstLiteralFloat {
    private final ValueType valueType;
    private final double value;

    IntermediateValue(SourcePosition sourcePosition, ValueType valueType, double value) {
        super(sourcePosition, Double.toString(value));
        this.valueType = valueType;
        this.value = value;
    }

    public double getAsDouble() {
        return value;
    }

    public @Nullable AstLiteral toNumericLiteral(InstructionProcessor processor) {
        return switch (valueType) {
            case NULL -> new AstLiteralNull(sourcePosition());
            case BOOLEAN -> new AstLiteralBoolean(sourcePosition(), value != 0);
            case BIN -> processor.isValidIntegerLiteral((long) value)
                    ? new AstLiteralBinary(sourcePosition(), addPrefix(Long.toString((long)value, 2), "0b"))
                    : null;
            case HEX -> processor.isValidHexLiteral((long) value)
                    ? new AstLiteralHexadecimal(sourcePosition(), addPrefix(Long.toString((long)value, 16), "0x"))
                    : null;
            case LONG -> processor.isValidIntegerLiteral((long) value)
                    ? new AstLiteralDecimal(sourcePosition(), String.valueOf((long) value))
                    : null;
            case DOUBLE -> {
                Optional<LogicLiteral> literal = processor.createLiteral(sourcePosition(), value, false);

                yield switch(literal.orElse(null)) {
                    case LogicNumber number -> new AstLiteralFloat(sourcePosition(), number.toMlog());
                    case LogicColor color   -> new AstLiteralColor(sourcePosition(), color.toMlog());
                    case null, default -> null;
                };
            }
        };
    }

    private String addPrefix(String literal, String prefix) {
        return prefix.isEmpty() ? literal
                : literal.startsWith("-") ? "-" + prefix + literal.substring(1)
                : prefix + literal;
    }

    @Override
    public IntermediateValue withSourcePosition(SourcePosition sourcePosition) {
        return new IntermediateValue(sourcePosition, valueType, value);
    }
}
