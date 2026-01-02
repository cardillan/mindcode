package info.teksol.mc.emulator.mimex.target80;

import info.teksol.mc.emulator.mimex.LStrings;
import info.teksol.mc.emulator.mimex.target70.LStrings70;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LStrings80 extends LStrings70 implements LStrings {

    protected double exp(long whole, long power) {
        return whole * Math.pow(10, power);
    }
}
