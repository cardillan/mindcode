package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.EmulatedProcessor;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.LExecutor;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.util.EnumUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@NullMarked
public class LogicBlock extends MindustryBuilding {
    private final boolean privileged;
    private final int ipt;
    private final Map<String, MindustryBuilding> linkedBlocks = new LinkedHashMap<>();
    private final String code;
    private @Nullable LExecutor executor;

    public LogicBlock(String name, BlockType type, BlockPosition position, String code) {
        super(name, type, position);
        this.code = code;
        switch (type.name()) {
            case "@micro-processor" -> { ipt = 2; privileged = false; }
            case "@logic-processor" -> { ipt = 8; privileged = false; }
            case "@hyper-processor" -> { ipt = 25; privileged = false; }
            case "@world-processor" -> { ipt = 8; privileged = true; }
            default -> { ipt = 1; privileged = false; }
        }
    }

    public static LogicBlock createProcessor(MindustryMetadata metadata, EmulatedProcessor processor, BlockPosition position, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock("@" + EnumUtils.toKebabCase(processor)), position, code);
    }

    public static LogicBlock createMicroProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, EmulatedProcessor.MICRO_PROCESSOR, position, code);
    }

    public static LogicBlock createLogicProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, EmulatedProcessor.LOGIC_PROCESSOR, position, code);
    }

    public static LogicBlock createHyperProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, EmulatedProcessor.HYPER_PROCESSOR, position, code);
    }

    public static LogicBlock createWorldProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, EmulatedProcessor.WORLD_PROCESSOR, position, code);
    }

    public void addBlock(String name, MindustryBuilding block) {
        linkedBlocks.put(name, block);
    }

    public String getCode() {
        return code;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public int getIpt() {
        return ipt;
    }

    public Map<String, MindustryBuilding> getLinks() {
        return linkedBlocks;
    }

    public void setExecutor(LExecutor executor) {
        this.executor = executor;
    }

    public String processorId() {
        return type.format() + " at (" + x() + ", " + y() + ")";
    }

    public @Nullable LVar getOptionalVar(String name) {
        assert executor != null;
        return executor.getOptionalVar(name);
    }
}
