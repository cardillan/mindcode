package info.teksol.mc.emulator.v2;

import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.processor.TextBuffer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LExecutor {
    LInstruction build(LAssembler assembler, LStatement statement);

    TextBuffer textBuffer();

    @Nullable MindustryBuilding getLink(int index);

    LInstruction[] instructions();

    LVar[] vars();

    LVar counter();

    LVar unit();

    LVar thisv();

    LVar ipt();
}
