package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.ExecutorResults;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.TextBuffer;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.mimex.target60.LExecutor60;
import info.teksol.mc.emulator.mimex.target70.LExecutor70;
import info.teksol.mc.emulator.mimex.target80.LExecutor80;
import info.teksol.mc.emulator.mimex.target81.LExecutor81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface LExecutor extends ExecutorResults {
    void load(LParser parser);

    void runTick(int executorIndex, double delta);

    boolean isEmpty();

    @Nullable LVar getOptionalVar(String name);

    boolean active();

    boolean finished();

    boolean yield();

    TextBuffer textBuffer();

    static LExecutor create(MindustryMetadata metadata, LAssembler assembler, BasicEmulator emulator, LogicBlock logicBlock) {
        return switch (metadata.getProcessorVersion()) {
            case V6         -> new LExecutor60(metadata, assembler, emulator, logicBlock);
            case V7, V7A    -> new LExecutor70(metadata, assembler, emulator, logicBlock);
            case V8A        -> new LExecutor80(metadata, assembler, emulator, logicBlock);
            case V8B, MAX   -> new LExecutor81(metadata, assembler, emulator, logicBlock);
        };
    }
}
