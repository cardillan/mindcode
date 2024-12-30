package info.teksol.mc.emulator;

public class MindustryString extends AbstractMindustryObject {

    public MindustryString(String name) {
        super(name, -1, null);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass() && ((MindustryString) obj).name().equals(name());
    }
}
