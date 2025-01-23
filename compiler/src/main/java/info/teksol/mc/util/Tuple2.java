package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Tuple2<E1 extends @Nullable Object, E2 extends @Nullable Object>(E1 e1, E2 e2) {

    public E1 e1() {
        return e1;
    }

    public E2 e2() {
        return e2;
    }

    public static <E1 extends @Nullable Object, E2 extends @Nullable Object> Tuple2<E1, E2> of(E1 e1, E2 e2) {
        return new Tuple2<>(e1, e2);
    }

    public static <E extends @Nullable Object> Tuple2<E, E> ofSame(E e1, E e2) {
        return new Tuple2<>(e1, e2);
    }
}
