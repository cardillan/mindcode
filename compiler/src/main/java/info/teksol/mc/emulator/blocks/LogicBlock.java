package info.teksol.mc.emulator.blocks;

import info.teksol.mc.emulator.LVar;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.emulator.mimex.LExecutor;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@NullMarked
public class LogicBlock extends MindustryBuilding {
    private final boolean privileged;
    private final int ipt;
    private final Map<String, MindustryBuilding> linkedBlocks = new LinkedHashMap<>();
    private final String code;
    private @Nullable LExecutor executor;

    public LogicBlock(String name, BlockType type, BlockPosition position, int ipt, boolean privileged, String code) {
        super(name, type, position);
        this.code = code;
        this.ipt = ipt;
        this.privileged = privileged;
    }

    public static LogicBlock createProcessor(MindustryMetadata metadata, ProcessorType processor, BlockPosition position, String code) {
        return new LogicBlock("processor", metadata.getExistingBlock(processor.emulatorTypeName()), position,
                processor.ipt(), processor.privileged(), code);
    }

    public static LogicBlock createMicroProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, ProcessorType.MICRO_PROCESSOR, position, code);
    }

    public static LogicBlock createLogicProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, ProcessorType.LOGIC_PROCESSOR, position, code);
    }

    public static LogicBlock createHyperProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, ProcessorType.HYPER_PROCESSOR, position, code);
    }

    public static LogicBlock createWorldProcessor(MindustryMetadata metadata, BlockPosition position, String code) {
        return createProcessor(metadata, ProcessorType.WORLD_PROCESSOR, position, code);
    }

    public void addBlock(String name, MindustryBuilding block) {
        linkedBlocks.put(name, block);
    }

    private void addBlocks(String name, Function<Integer, MindustryBuilding> creator) {
        for (int i = 1; i < 10; i++) {
            addBlock(name + i, creator.apply(i));
        }
    }

    public void createDefaultBlocks(MindustryMetadata metadata) {
        addBlocks("cell", _ -> MemoryBlock.createMemoryCell(metadata, BlockPosition.ZERO_POSITION));
        addBlocks("bank", _ -> MemoryBlock.createMemoryBank(metadata, BlockPosition.ZERO_POSITION));
        addBlocks("display", i -> LogicDisplay.createLogicDisplay(metadata, i < 5, BlockPosition.ZERO_POSITION));
        addBlocks("message", _ -> MessageBlock.createMessage(metadata, BlockPosition.ZERO_POSITION));
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
