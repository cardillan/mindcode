package info.teksol.mc.emulator.v2;

import org.jspecify.annotations.NullMarked;

// Creates variables
@NullMarked
public interface LAssembler {

    LVar var(String symbol);
}
