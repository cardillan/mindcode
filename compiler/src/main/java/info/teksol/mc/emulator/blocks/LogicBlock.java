package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.EmulatedProcessor;
import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.mimex.LExecutor;
import info.teksol.mc.mindcode.logic.mimex.MindustryContent;
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

    public LogicBlock(String name, MindustryContent type, String code) {
        super(name, type);
        this.code = code;
        switch (type.name()) {
            case "@micro-processor" -> { ipt = 2; privileged = false; }
            case "@logic-processor" -> { ipt = 8; privileged = false; }
            case "@hyper-processor" -> { ipt = 25; privileged = false; }
            case "@world-processor" -> { ipt = 8; privileged = true; }
            default -> { ipt = 1; privileged = false; }
        }
    }

    public static LogicBlock createMicroProcessor(MindustryMetadata metadata, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock("@micro-processor"), code);
    }

    public static LogicBlock createLogicProcessor(MindustryMetadata metadata, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock("@logic-processor"), code);
    }

    public static LogicBlock createHyperProcessor(MindustryMetadata metadata, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock("@hyper-processor"), code);
    }

    public static LogicBlock createWorldProcessor(MindustryMetadata metadata, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock("@world-processor"), code);
    }

    public static LogicBlock createProcessor(MindustryMetadata metadata, EmulatedProcessor processor, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock("@" + EnumUtils.toKebabCase(processor)), code);
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

    public @Nullable LVar getOptionalVar(String name) {
        assert executor != null;
        return executor.getOptionalVar(name);
    }
}
