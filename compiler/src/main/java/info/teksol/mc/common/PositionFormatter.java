package info.teksol.mc.common;

import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@FunctionalInterface
@NullMarked
public interface PositionFormatter extends Function<InputPosition, String> {
    @Override
    default String apply(InputPosition position) {
        return formatPosition(position);
    }

    String formatPosition(InputPosition position);
}
