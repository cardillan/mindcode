package io.github.cardillan.mindcode.emulator.mimex.target82;

import io.github.cardillan.mindcode.emulator.blocks.LogicBlock;
import io.github.cardillan.mindcode.emulator.mimex.BasicEmulator;
import io.github.cardillan.mindcode.emulator.mimex.LAssembler;
import io.github.cardillan.mindcode.emulator.mimex.target81.LExecutor81;
import io.github.cardillan.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LExecutor82 extends LExecutor81 {

    public LExecutor82(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        super(metadata, assembler, emulator, logicBlock);
    }
}
