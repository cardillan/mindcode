package info.teksol.mc.emulator.v2;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface MlogWritable {
    void setDoubleValue(double value);

    void setLongValue(long value);

    void setBooleanValue(boolean value);
}
