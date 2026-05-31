package info.teksol.mc.emulator;

import info.teksol.mc.evaluator.ExpressionEvaluator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface MlogReadable {
    @Nullable Object getObject();

    double getDoubleValue();

    long getLongValue();

    boolean isObject();

    default boolean getBooleanValue() {
        return !ExpressionEvaluator.equals(getDoubleValue(), 0.0);
    }
}
