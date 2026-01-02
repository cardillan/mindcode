package info.teksol.mc.emulator;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public interface LInstruction {
    void run();
    List<LVar> vars();
    String opcode();
}
