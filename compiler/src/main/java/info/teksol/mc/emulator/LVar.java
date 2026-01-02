package info.teksol.mc.emulator;

import info.teksol.mc.emulator.blocks.MindustryBuilding;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LVar implements MlogReadable, MlogWritable {
    public final String name;
    public boolean constant;
    public boolean isobj;
    public @Nullable Object objval;
    public double numval;

    //ms timestamp for when this was last synced; used in the sync instruction
    public long syncTime;

    // Number of the last execution step which updated this value
    // and the previous values of these fields
    public int updateStep;

    public LVar(String name) {
        this(name, false);
    }

    public LVar(String name, boolean constant) {
        this.name = name;
        this.constant = constant;
    }

    public LVar link(MindustryBuilding block) {
        isobj = true;
        objval = block;
        numval = 0;
        constant = true;
        return this;
    }

    public LVar unlink() {
        isobj = false;
        objval = null;
        numval = 0;
        constant = false;
        return this;
    }

    public @Nullable Object obj() {
        return isobj ? objval : null;
    }

    public double num() {
        return isobj ? objval != null ? 1 : 0 : invalid(numval) ? 0 : numval;
    }

    public int numi() {
        return (int) num();
    }

    public void setnum(double value) {
        if (constant) return;
        if (invalid(value)) {
            objval = null;
            isobj = true;
        } else {
            numval = value;
            objval = null;
            isobj = false;
        }
    }

    public void setobj(@Nullable Object value) {
        if (constant) return;
        objval = value;
        isobj = true;
    }

    public void set(LVar other) {
        isobj = other.isobj;
        // Setting a non-numeric value to @counter must preserve its numeric field
        if (isobj) {
            objval = other.objval;
        } else {
            numval = invalid(other.numval) ? 0 : other.numval;
        }
    }

    public boolean invalidNumber() {
         return isobj && objval != null || invalid(numval);
    }

    public static boolean invalid(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    @Override
    public @Nullable Object getObject() {
        return isobj ? objval : null;
    }

    @Override
    public double getDoubleValue() {
        return isobj ? objval != null ? 1 : 0 : invalid(numval) ? 0 : numval;
    }

    @Override
    public long getLongValue() {
        return isobj ? objval != null ? 1 : 0 : invalid(numval) ? 0 : (long) numval;
    }

    @Override
    public boolean isObject() {
        return isobj;
    }

    @Override
    public void setDoubleValue(double value) {
        setnum(value);
    }

    @Override
    public void setLongValue(long value) {
        setnum(value);
    }

    @Override
    public void setBooleanValue(boolean value) {
        setnum(value ? 1 : 0);
    }

    public String printExact() {
        return isobj ? print(objval) : String.valueOf(numval);
    }

    public String trace() {
        if (isobj) {
            return switch(objval) {
                case String str -> '"' + str.replaceAll("\n", "\\\\n") + '"';
                case MindustryObject obj -> obj.format();
                case Object obj -> obj.toString();
                case null -> "null";
            };
        } else {
            return String.valueOf(numval);
        }
    }

    public static String print(@Nullable Object object) {
        return switch (object) {
            case MindustryObject obj -> obj.format();
            case Object obj -> obj.toString();
            case null -> "null";
        };
    }

    @Override
    public String toString() {
        return name + " = " + printExact() + (constant ? " [const]" : "");
    }

    public static LVar create(String name) {
        return new LVar(name);
    }
}
