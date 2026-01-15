package info.teksol.schemacode.schematics;

import info.teksol.mc.emulator.EmulatedProcessor;
import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.blocks.*;
import info.teksol.mc.emulator.blocks.BlockPosition;
import info.teksol.mc.emulator.blocks.graphics.LogicDisplay;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
public record Schematic(String name, String filename, String description, List<String> labels, int width, int height, List<Block> blocks) {

    public static Schematic empty() {
        return new Schematic("Empty", "", "", List.of(), 0, 0, List.of());
    }

    public EmulatorSchematic toEmulatorSchematic(MindustryMetadata metadata) {
        Map<Position, MindustryBuilding> blockMap = blocks.stream()
                .collect(Collectors.toMap(Block::position, b -> convertBlock(metadata, b)));

        for (Block block : blocks) {
            if (blockMap.get(block.position()) instanceof LogicBlock logicBlock) {
                setupLinks(logicBlock, block.configuration().as(ProcessorConfiguration.class), blockMap);
            }
        }

        return new EmulatorSchematic(List.copyOf(blockMap.values()));
    }

    private MindustryBuilding convertBlock(MindustryMetadata metadata, Block block) {
        BlockPosition position = block.position().toBlockPosition();
        return switch (block.blockType().name()) {
            case "@micro-processor" -> createLogicBlock(metadata, block, EmulatedProcessor.MICRO_PROCESSOR);
            case "@logic-processor" -> createLogicBlock(metadata, block, EmulatedProcessor.LOGIC_PROCESSOR);
            case "@hyper-processor" -> createLogicBlock(metadata, block, EmulatedProcessor.HYPER_PROCESSOR);
            case "@world-processor" -> createLogicBlock(metadata, block, EmulatedProcessor.WORLD_PROCESSOR);
            case "@memory-cell" -> MemoryBlock.createMemoryCell(metadata, position);
            case "@memory-bank" -> MemoryBlock.createMemoryBank(metadata, position);
            case "@message" -> MessageBlock.createMessage(metadata, position);
            case "@logic-display" -> LogicDisplay.createLogicDisplay(metadata, position);
            case "@large-logic-display" -> LogicDisplay.createLargeLogicDisplay(metadata, position);
            default -> new MindustryBuilding(block.blockType().getBaseLinkName(), metadata.getExistingBlock(block.name()), position);
        };
    }

    private LogicBlock createLogicBlock(MindustryMetadata metadata, Block block, EmulatedProcessor processor) {
        return LogicBlock.createProcessor(metadata, processor, block.position().toBlockPosition(),
                block.configuration().as(ProcessorConfiguration.class).code());
    }

    private void setupLinks(LogicBlock logicBlock, ProcessorConfiguration configuration, Map<Position, MindustryBuilding> blockMap) {
        configuration.links().forEach(link -> logicBlock.addBlock(link.name(), blockMap.get(link.position())));
    }
}
