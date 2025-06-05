package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.evaluator.LogicWritable;
import info.teksol.mc.mindcode.compiler.ast.nodes.AstMindcodeNode;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import org.jspecify.annotations.NullMarked;

/// This class represents a container for a single value that accept either a boolean,
/// long, double, or null. This class is used as an intermediary for compile-time evaluation.
@NullMarked
class Result implements LogicWritable {
    private ValueType valueType = ValueType.NULL;
    private double value = 0.0;

    @Override
    public void setDoubleValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            this.value = 0.0;
            this.valueType = ValueType.NULL;
        } else {
            this.value = value;
            this.valueType = ValueType.DOUBLE;
        }
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

    public AstMindcodeNode toAstMindcodeNode(InstructionProcessor processor, AstMindcodeNode source, int radix) {
        ValueType type = switch (radix) {
            case 2 -> ValueType.BIN;
            case 16 -> ValueType.HEX;
            default -> valueType;
        };

        return new IntermediateValue(source.sourcePosition(), type, value);
    }

    public AstMindcodeNode toAstMindcodeNode(InstructionProcessor processor, AstMindcodeNode source) {
        return new IntermediateValue(source.sourcePosition(), valueType, value);
    }
}
