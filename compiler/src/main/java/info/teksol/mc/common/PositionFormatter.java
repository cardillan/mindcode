package info.teksol.mc.common;

import org.jspecify.annotations.NullMarked;

import java.util.function.Function;

@FunctionalInterface
@NullMarked
public interface PositionFormatter extends Function<SourcePosition, String> {
    @Override
    default String apply(SourcePosition position) {
        return formatPosition(position);
    }

    String formatPosition(SourcePosition position);
}
