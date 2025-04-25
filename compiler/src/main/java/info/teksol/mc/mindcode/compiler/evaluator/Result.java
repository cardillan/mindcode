package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.evaluator.LogicWritable;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import org.jspecify.annotations.NullMarked;

/// This class represents a container for a single value that accept either a boolean,
/// long, double, or null. This class is used as an intermediary for compile-time evaluation.
@NullMarked
class Result implements LogicWritable {
    private enum ValueType {NULL, BOOLEAN, LONG, DOUBLE}

    private ValueType valueType = ValueType.NULL;
    private double value = 0.0;

    @Override
    public void setDoubleValue(double value) {
        this.value = value;
        this.valueType = ValueType.DOUBLE;
    }

    @Override
    public void setLongValue(long value) {
        this.value = value;
        this.valueType = ValueType.LONG;
    }

    @Override
    public void setBooleanValue(boolean value) {
        this.value = value ? 1.0 : 0.0;
        this.valueType = ValueType.BOOLEAN;
    }

    public AstLiteral toAstMindcodeNode(AstMindcodeNode source, int radix) {
        return switch (radix) {
            case 2  -> new AstLiteralBinary(source.sourcePosition(), addPrefix(Long.toString((long)value, radix), "0b"));
            case 16 -> new AstLiteralHexadecimal(source.sourcePosition(), addPrefix(Long.toString((long)value, radix), "0x"));
            default -> switch (valueType) {
                case NULL       -> new AstLiteralNull(source.sourcePosition());
                case BOOLEAN    -> new AstLiteralBoolean(source.sourcePosition(), value != 0);
                case LONG       -> new AstLiteralDecimal(source.sourcePosition(), String.valueOf((long) value));
                case DOUBLE     -> Double.isNaN(value) || Double.isInfinite(value)
                        ? new AstLiteralNull(source.sourcePosition())
                        : new IntermediateValue(source.sourcePosition(), value);
            };
        };
    }

    private String addPrefix(String literal, String prefix) {
        return prefix.isEmpty() ? literal
                : literal.startsWith("-") ? "-" + prefix + literal.substring(1)
                : prefix + literal;
    }

    public AstLiteral toAstMindcodeNode(AstMindcodeNode source) {
        return toAstMindcodeNode(source, 10);
    }
}
