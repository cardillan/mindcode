package io.github.cardillan.mindcode.emulator;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MlogWritable {
    void setDoubleValue(double value);

    void setLongValue(long value);

    void setBooleanValue(boolean value);
}
