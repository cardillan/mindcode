package info.teksol.schemacode.schematics;

import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.config.*;
import info.teksol.schemacode.mindustry.*;
import info.teksol.schemacode.mindustry.ProcessorConfiguration.Link;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Decompiler {
    private boolean relativePositions = false;
    private boolean relativeConnections = true;
    private boolean relativeLinks = false;
    private final boolean orderedLinks = true;
    private BlockOrder blockOrder = BlockOrder.ORIGINAL;
    private DirectionLevel directionLevel = DirectionLevel.ROTATABLE;

    private final StringBuilder sbr = new StringBuilder();
    private int indent = 0;
    private String strIndent = "";

    private final Schematic schematic;
    private final Map<Position, Block> blocks;
    private final List<ProcessorConfiguration> processors;
    private final boolean useProcessorPrefix;

    public boolean isRelativePositions() {
        return relativePositions;
    }

    public void setRelativePositions(boolean relativePositions) {
        this.relativePositions = relativePositions;
    }

    public boolean isRelativeConnections() {
        return relativeConnections;
    }

    public void setRelativeConnections(boolean relativeConnections) {
        this.relativeConnections = relativeConnections;
    }

    public boolean isRelativeLinks() {
        return relativeLinks;
    }

    public void setRelativeLinks(boolean relativeLinks) {
        this.relativeLinks = relativeLinks;
    }

    public BlockOrder getBlockOrder() {
        return blockOrder;
    }

    public void setBlockOrder(BlockOrder blockOrder) {
        this.blockOrder = blockOrder;
    }

    public DirectionLevel getDirectionLevel() {
        return directionLevel;
    }

    public void setDirectionLevel(DirectionLevel directionLevel) {
        this.directionLevel = directionLevel;
    }

    public Decompiler(Schematic schematic) {
        setIndent(0);

        this.schematic = schematic;
        blocks = schematic.blocks().stream().collect(Collectors.toMap(Block::position, b -> b));
        processors = schematic.blocks().stream()
                .filter(b -> b.configurationType() == ConfigurationType.PROCESSOR)
                .map(b -> b.configuration().as(ProcessorConfiguration.class))
                .toList();
        useProcessorPrefix = processors.size() > 1;
    }

    private String getLinkName(ProcessorConfiguration processor, Link link) {
        Block linkBlock = blocks.get(link.position());
        if (linkBlock == null) {
            return link.name() + " virtual";
        } else if (useProcessorPrefix) {
            int index = processors.indexOf(processor);
            return "p" + index + "-" + link.name();
        } else {
            return link.name();
        }
    }

    private Optional<String> getLinkedBlockName(ProcessorConfiguration processor, Position position) {
        return processor.links().stream()
                .filter(l -> l.position().equals(position))
                .map(l -> getLinkName(processor, l))
                .findFirst();
    }

    private List<String> linkNames(Position position) {
        return processors.stream().flatMap(p -> getLinkedBlockName(p, position).stream()).toList();
    }

    public String buildCode() {
        sbr.append("schematic");
        indentInc();
        nl().append("name = \"").append(schematic.name()).append('"');
        if (!schematic.description().isBlank()) {
            nl().append("description = \"\"\"");
            indentInc();
            nl().append(schematic.description().replaceAll("\n", strIndent)).append("\"\"\"");
            indentDec();
        }

        schematic.labels().stream()
                .mapMulti(this::extractLabelsAndIcons)
                .filter(t -> !t.isBlank())
                .distinct()
                .forEach(t -> nl().append("tag = ").append(SchematicsMetadata.metadata.getIcons().decodeIcon(t)));

        sbr.append('\n');

        (switch (blockOrder) {
            case ORIGINAL   -> schematic.blocks().stream();
            case HORIZONTAL -> schematic.blocks().stream().sorted(Comparator.comparing(Block::y).thenComparing(Block::x));
            case VERTICAL   -> schematic.blocks().stream().sorted(Comparator.comparing(Block::x).thenComparing(Block::y));
        }).forEach(this::outputBlock);

        indentDec();
        nl().append("end");
        nl();

        for (int index = 0; index < processors.size(); index++) {
            nl().append("mlog-").append(index).append(" = \"\"\"");
            indentInc();
            nl().append(processors.get(index).code().replaceAll("\n", strIndent)).append("\"\"\"");
            indentDec();
            nl();
        }

        return sbr.toString();
    }

    private void extractLabelsAndIcons(String label, Consumer<String> consumer) {
        consumer.accept(label);
        for (int i = 0; i < label.length(); i++) {
            char ch = label.charAt(i);
            if (ch >= 32768) {
                String icon = String.valueOf(ch);
                if (SchematicsMetadata.metadata.getIcons().isIconValue(icon)) {
                    consumer.accept(icon);
                }
            }
        }
    }

    private Block lastBlock;

    private void outputBlock(Block block) {
        Position pos = block.position();
        List<String> linkNames = linkNames(pos);
        if (!linkNames.isEmpty()) {
            sbr.append("\n");
            for (int i = 0; i < linkNames.size(); i++) {
                sbr.append(linkNames.get(i)).append(i == linkNames.size() - 1 ? ":" : ", ");
            }
        }

        nl().append(String.format("%-20s", block.name())).append(" at ")
                .append(pos.toStringNear(relativePositions && lastBlock != null ? lastBlock.position() : null));

        boolean appendFacing = switch (directionLevel) {
            case ROTATABLE -> block.blockType().rotate();
            case NON_DEFAULT -> block.direction() != Direction.EAST;
            case ALL -> true;
        };

        if (appendFacing) {
            sbr.append(" facing ").append(block.direction().toSchemacode());
        }

        if (block.configuration() != EmptyConfiguration.EMPTY) {
            outputConfiguration(block);
        }

        lastBlock = block;
    }

    private void outputConfiguration(Block block) {
        Class<? extends Configuration> cfgClass = block.configurationClass();
        Configuration cfg = block.configuration().as(cfgClass);
        switch (cfg) {
            case BlockConfiguration b       -> sbr.append(" block ").append(b.getName());
            case BooleanConfiguration b     -> sbr.append(b.value() ? " enabled" : " disabled");
            case Color c                    -> writeColor(c);
            case ItemConfiguration i        -> sbr.append(" item ").append(i.getName());
            case LiquidConfiguration l      -> sbr.append(" liquid ").append(l.getName());
            case Position p                 -> writeConnection(block, p);
            case PositionArray p            -> writeConnections(block, p);
            case ProcessorConfiguration p   -> writeProcessor(block, p);
            case TextConfiguration t        -> writeText(t);
            case UnitConfiguration u        -> sbr.append(" unit ").append(u.getName());
            case UnitCommandConfiguration c -> sbr.append(" command ").append(c.getName());
            case UnitPlan p                 -> sbr.append(" unit ").append(p.unitName());
            default                         -> sbr.append(" // unknown configuration: ").append(cfg);
        }
    }

    private void writeColor(Color color) {
        sbr.append(" color rgba(")
                .append(color.red()).append(", ")
                .append(color.green()).append(", ")
                .append(color.blue()).append(", ")
                .append(color.alpha())
                .append(')');
    }

    private void writeConnection(Block block, Position p) {
        if (p.nonNegative()) {
            sbr.append(" connected to ").append(p.toString(relativeConnections ? block.position() : null));
        }
    }

    private void writeConnections(Block block, PositionArray p) {
        // TODO use symbolic names where possible
        List<Position> positions = p.positions();
        if (!positions.isEmpty()) {
            String nodes = positions.stream()
                    .map(pos -> pos.toString(relativeConnections ? block.position() : null))
                    .collect(Collectors.joining(", "));
            sbr.append(" connected to ").append(nodes);
        }
    }

    private void writeProcessor(Block block, ProcessorConfiguration processor) {
        int index = processors.indexOf(processor);
        sbr.append(" processor");
        indentInc();
        nl().append("links");
        indentInc();
        if (orderedLinks) {
            processor.links().forEach(l -> writeLink(block, processor, l));
        } else {
            nl().append(useProcessorPrefix ? "p" + index + "-*" : "*");
            processor.links().stream()
                    .filter(l -> !blocks.containsKey(l.position()))
                    .forEach(l -> nl().append(l.position().toString(relativeLinks ? block.position() : null))
                            .append(" as ").append(l.name()).append(" virtual"));
        }
        indentDec();
        nl().append("end");

        nl().append("mlog = mlog-").append(processors.indexOf(processor));

        indentDec();
        nl().append("end");
    }

    private void writeLink(Block block, ProcessorConfiguration processor, Link link) {
        Block linked = blocks.get(link.position());
        if (linked == null) {
            // Virtual link
            nl().append(link.position().toString(relativeLinks ? block.position() : null))
                    .append(" as ").append(link.name()).append(" virtual");
        } else {
            Optional<String> label = getLinkedBlockName(processor, link.position());
            nl().append(label.orElse(link.position().toString(relativeLinks ? block.position() : null)))
                    .append(" as ").append(link.name());
        }
    }

    private void writeText(TextConfiguration text) {
        if (text.value().contains("\n")) {
            sbr.append(" text \"\"\"");
            indentInc();
            nl().append(text.value().replaceAll("\n", strIndent)).append("\"\"\"");
            indentDec();
        } else {
            sbr.append(" text \"").append(text.value()).append("\"");
        }
    }

    private StringBuilder nl() {
        return sbr.append(strIndent);
    }

    private void setIndent(int indent) {
        this.indent = indent;
        char[] charArray = new char[indent * 4 + 1];
        Arrays.fill(charArray, ' ');
        charArray[0] = '\n';
        strIndent = new String(charArray);
    }

    private void indentInc()  {
        setIndent(indent + 1);
    }

    private void indentDec()  {
        setIndent(indent == 0 ? 0 : indent - 1);
    }
}
