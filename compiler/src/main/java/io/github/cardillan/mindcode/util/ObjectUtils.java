package io.github.cardillan.mindcode.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ObjectUtils {

    public static <E> E toNonNull(@Nullable E e) {
        assert e != null;
        return e;
    }
}
