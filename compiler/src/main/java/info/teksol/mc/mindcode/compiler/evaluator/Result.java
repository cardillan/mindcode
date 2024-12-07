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

    public AstLiteral toAstMindcodeNode(AstMindcodeNode source) {
        return switch (valueType) {
            case NULL       -> new AstLiteralNull(source.sourcePosition());
            case BOOLEAN    -> new AstLiteralBoolean(source.sourcePosition(), value != 0);
            case LONG       -> new AstLiteralDecimal(source.sourcePosition(), String.valueOf((long) value));
            case DOUBLE     -> Double.isNaN(value) || Double.isInfinite(value)
                    ? new AstLiteralNull(source.sourcePosition())
                    : new IntermediateValue(source.sourcePosition(), value);
        };
    }
}
