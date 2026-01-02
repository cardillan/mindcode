package info.teksol.mc.emulator.v2;

import info.teksol.mc.emulator.blocks.MindustryBuilding;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LVar implements MlogReadable, MlogWritable {
    public final String name;
    public boolean isobj;
    public boolean constant;
    public @Nullable Object objval;
    public double numval;

    //ms timestamp for when this was last synced; used in the sync instruction
    public long syncTime;

    public LVar(String name) {
        this(name, false);
    }

    public LVar(String name, boolean constant) {
        this.name = name;
        this.constant = constant;
    }

    public @Nullable MindustryBuilding building() {
        return isobj && objval instanceof MindustryBuilding building ? building : null;
    }

    public @Nullable Object obj() {
        return isobj ? objval : null;
    }

//    public @Nullable Team team(){
//        if(isobj){
//            return objval instanceof Team t ? t : null;
//        }else{
//            int t = (int)numval;
//            if(t < 0 || t >= Team.all.length) return null;
//            return Team.all[t];
//        }
//    }

    public boolean bool() {
        return isobj ? objval != null : Math.abs(numval) >= 0.00001;
    }

    public double num() {
        return isobj ? objval != null ? 1 : 0 : invalid(numval) ? 0 : numval;
    }

    /**
     * Get num value from variable, convert null to NaN to handle it differently in some instructions
     */
    public double numOrNan() {
        return isobj ? objval != null ? 1 : Double.NaN : invalid(numval) ? 0 : numval;
    }

    public float numf() {
        return isobj ? objval != null ? 1 : 0 : invalid(numval) ? 0 : (float) numval;
    }

    /**
     * Get float value from variable, convert null to NaN to handle it differently in some instructions
     */
    public float numfOrNan() {
        return isobj ? objval != null ? 1 : Float.NaN : invalid(numval) ? 0 : (float) numval;
    }

    public int numi() {
        return (int) num();
    }

    public void setbool(boolean value) {
        setnum(value ? 1 : 0);
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

    public void setconst(Object value) {
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
         return isobj || invalid(numval);
    }

    public static boolean invalid(double d) {
        return Double.isNaN(d) || Double.isInfinite(d);
    }

    @Override
    public String toString() {
        return name + ": " + (isobj ? objval : numval) + (constant ? " [const]" : "");
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
}
