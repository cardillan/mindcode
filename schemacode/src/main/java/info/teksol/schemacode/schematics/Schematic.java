package info.teksol.schemacode.schematics;

import info.teksol.mc.emulator.EmulatorSchematic;
import info.teksol.mc.emulator.blocks.LogicBlock;
import info.teksol.mc.emulator.blocks.MemoryBlock;
import info.teksol.mc.emulator.blocks.MessageBlock;
import info.teksol.mc.emulator.blocks.MindustryBuilding;
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
        return switch (block.blockType().name()) {
            case "@micro-processor" -> LogicBlock.createMicroProcessor(metadata, block.configuration().as(ProcessorConfiguration.class).code());
            case "@logic-processor" -> LogicBlock.createLogicProcessor(metadata, block.configuration().as(ProcessorConfiguration.class).code());
            case "@hyper-processor" -> LogicBlock.createHyperProcessor(metadata, block.configuration().as(ProcessorConfiguration.class).code());
            case "@world-processor" -> LogicBlock.createWorldProcessor(metadata, block.configuration().as(ProcessorConfiguration.class).code());
            case "@memory-cell" -> MemoryBlock.createMemoryCell(metadata);
            case "@memory-bank" -> MemoryBlock.createMemoryBank(metadata);
            case "@message" -> MessageBlock.createMessage(metadata);
            case "@logic-display" -> LogicDisplay.createLogicDisplay(metadata);
            case "@large-logic-display" -> LogicDisplay.createLargeLogicDisplay(metadata);
            default -> new MindustryBuilding(block.blockType().getBaseLinkName(), metadata.getExistingBlock(block.name()));
        };
    }

    private void setupLinks(LogicBlock logicBlock, ProcessorConfiguration configuration, Map<Position, MindustryBuilding> blockMap) {
        configuration.links().forEach(link -> logicBlock.addBlock(link.name(), blockMap.get(link.position())));
    }
}
