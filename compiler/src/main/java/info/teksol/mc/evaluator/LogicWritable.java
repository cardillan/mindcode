package info.teksol.mc.evaluator;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LogicWritable {
    void setDoubleValue(double value);

    void setLongValue(long value);

    void setBooleanValue(boolean value);
}
