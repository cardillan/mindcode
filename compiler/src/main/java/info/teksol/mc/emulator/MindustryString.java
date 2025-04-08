package info.teksol.mc.emulator;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MindustryString extends AbstractMindustryObject {

    public MindustryString(String name) {
        super(name, -1, null);
    }

    public String value() {
        return name();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass() && ((MindustryString) obj).name().equals(name());
    }
}
