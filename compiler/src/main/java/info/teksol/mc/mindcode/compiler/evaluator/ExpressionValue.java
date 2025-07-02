package info.teksol.mc.mindcode.compiler.evaluator;

import info.teksol.mc.evaluator.LogicReadable;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.ast.nodes.*;
import info.teksol.mc.mindcode.logic.instructions.InstructionProcessor;
import info.teksol.mc.mindcode.logic.mimex.LVar;
import info.teksol.mc.profile.BuiltinEvaluation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// The ExpressionValue class holds an immutable value that can be passed to expression evaluator.
/// Only nulls, numeric values and String objects are supported, and strings are contained as a plain
/// string, not wrapped into `MindustryString` instance.
///
/// The class is not used outside the compile time evaluator.
@NullMarked
class ExpressionValue implements LogicReadable {
    private final InstructionProcessor processor;
    private final @Nullable Object object;
    private final @Nullable Number value;

    /**
     * Creates an ExpressionValue instance holding compile-time value of the given AST node.
     * When the node can't be compile-time evaluated, an invalid instance is returned.
     *
     * @param processor the instruction processor for interpreting icon identifiers.
     * @param node the abstract syntax tree node holding the value of the expression.
     * @return an ExpressionValue instance representing the value of the given node.
     */
    public static @NonNull ExpressionValue create(BuiltinEvaluation builtinEvaluation, InstructionProcessor processor, AstMindcodeNode node) {
        try {
            return switch (node) {
                case AstLiteralNull n -> new ExpressionValue(processor, null, null);
                case AstLiteralBoolean n -> new ExpressionValue(processor, null, n.getValue() ? 1 : 0);
                case AstLiteralDecimal n -> new ExpressionValue(processor, null, n.getLongValue());
                case AstLiteralBinary n -> new ExpressionValue(processor, null, n.getLongValue());
                case AstLiteralHexadecimal n -> new ExpressionValue(processor, null, n.getLongValue());
                case AstLiteralFloat n -> new ExpressionValue(processor, null, n.getDoubleValue());
                case AstLiteralColor n -> new ExpressionValue(processor, null, n.getDoubleValue());
                case AstLiteralString n -> new ExpressionValue(processor, n.getValue(), null);
                case AstLiteralChar n -> new ExpressionValue(processor, null, n.getLongValue());
                case AstIdentifier n -> processor.getMetadata().getIcons().isIconName(n.getName())
                        ? new ExpressionValue(processor, processor.getMetadata().getIcons().getIconValue(n.getName()).format(processor), null)
                        : new InvalidValue(processor);
                case AstLiteral n ->
                        throw new MindcodeInternalError("Unhandled constant node " + node.getClass().getSimpleName());
                case AstBuiltInIdentifier b -> evaluateBuiltin(builtinEvaluation, processor, b);
                default -> new InvalidValue(processor);
            };
        } catch (NumberFormatException e) {
            return new InvalidValue(processor);
        }
    }

    private static @NonNull ExpressionValue evaluateBuiltin(BuiltinEvaluation builtinEvaluation, InstructionProcessor processor,
            AstBuiltInIdentifier b) {
        if (builtinEvaluation != BuiltinEvaluation.NONE) {
            LVar var = processor.getMetadata().getLVar(b.getName());
            return var != null && var.isNumericConstant()
                   && (builtinEvaluation == BuiltinEvaluation.FULL || processor.getMetadata().isStableBuiltin(var.name()))
                    ? new ExpressionValue(processor, null, var.numericValue()) : new InvalidValue(processor);
        } else {
            return new InvalidValue(processor);
        }
    }

    public static @NonNull ExpressionValue zero(InstructionProcessor processor) {
        return new ExpressionValue(processor, null, 0);
    }

    public static @NonNull ExpressionValue fromLiteral(InstructionProcessor processor, String literal) {
        return new ExpressionValue(processor, null, processor.parseNumber(literal));
    }

    private ExpressionValue(InstructionProcessor processor, @Nullable Object object, @Nullable Number value) {
        this.processor = processor;
        this.object = object;
        this.value = value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public double getDoubleValue() {
        return value == null ? 0.0 : value.doubleValue();
    }

    @Override
    public long getLongValue() {
        return value == null ? 0 : value.longValue();
    }

    @Override
    public @Nullable Object getObject() {
        return object;
    }

    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isObject() {
        return value == null;
    }

    public boolean isNull() {
        return object == null && value == null;
    }

    public boolean isString() {
        return object instanceof String;
    }

    public String print() {
        return isNull() ? "null" : object instanceof String string ? string : processor.formatNumber(getDoubleValue());
    }

    private static class InvalidValue extends ExpressionValue {
        public InvalidValue(InstructionProcessor processor) {
            super(processor, null, null);
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public double getDoubleValue() {
            throw new MindcodeInternalError("Trying to evaluate invalid value.");
        }

        @Override
        public long getLongValue() {
            throw new MindcodeInternalError("Trying to evaluate invalid value.");
        }

        @Override
        public Object getObject() {
            throw new MindcodeInternalError("Trying to evaluate invalid value.");
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public boolean isObject() {
            throw new MindcodeInternalError("Trying to evaluate invalid value.");
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public String print() {
            throw new MindcodeInternalError("Trying to evaluate invalid value.");
        }
    }
}
