package info.teksol.mc.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

@NullMarked
public class Lazy<T> {
    private final Supplier<T> supplier;
    private @Nullable T instance;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (instance == null) {
            instance = supplier.get();
        }
        return instance;
    }
}
