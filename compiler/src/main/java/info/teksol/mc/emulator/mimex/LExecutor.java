package info.teksol.mc.emulator.mimex;

import info.teksol.mc.emulator.Assertion;
import info.teksol.mc.emulator.LInstruction;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.TextBuffer;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
import info.teksol.mc.emulator.mimex.target60.LExecutor60;
import info.teksol.mc.emulator.mimex.target70.LExecutor70;
import info.teksol.mc.emulator.mimex.target80.LExecutor80;
import info.teksol.mc.emulator.mimex.target81.LExecutor81;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public interface LExecutor {
    void addBlock(String name, MindustryBuilding block);

    void runOnce(int stepLimit);

    List<LInstruction> instructions();

    @Nullable
    LVar getOptionalVar(String name);

    Map<String, LVar> getVars();

    void load(LParser parser);

    boolean finished();

    boolean yield();

    int steps();

    int noopSteps();

    List<Assertion> getAssertions();

    int[] getProfile();

    TextBuffer textBuffer();

    @Nullable MindustryBuilding getLink(int index);

    static LExecutor create(MindustryMetadata metadata, LAssembler assembler, MimexEmulator emulator) {
        return switch (metadata.getProcessorVersion()) {
            case V6         -> new LExecutor60(metadata, assembler, emulator);
            case V7, V7A    -> new LExecutor70(metadata, assembler, emulator);
            case V8A        -> new LExecutor80(metadata, assembler, emulator);
            case V8B, MAX   -> new LExecutor81(metadata, assembler, emulator);
        };
    }
}
