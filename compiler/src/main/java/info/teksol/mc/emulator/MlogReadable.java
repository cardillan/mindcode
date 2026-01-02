package info.teksol.mc.emulator;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface MlogReadable {
    @Nullable Object getObject();

    double getDoubleValue();

    long getLongValue();

    boolean isObject();
}
