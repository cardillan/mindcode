package info.teksol.mc.emulator.mimex.target82;

import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.mimex.BasicEmulator;
import info.teksol.mc.emulator.mimex.LAssembler;
import info.teksol.mc.emulator.mimex.target81.LExecutor81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LExecutor82 extends LExecutor81 {

    public LExecutor82(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        super(metadata, assembler, emulator, logicBlock);
    }
}
