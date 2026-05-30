package info.teksol.mc.emulator.mimex;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class LabelStatement extends LStatement {
    // Label, including colon
    private final String label;

    public LabelStatement(String label) {
        super(false, "", List.of(), List.of());
        this.label = label;
    }

    @Override
    public boolean virtual() {
        return true;
    }

    @Override
    public String toString() {
        return label;
    }
}
