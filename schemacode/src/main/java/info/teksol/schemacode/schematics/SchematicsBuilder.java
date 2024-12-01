package info.teksol.schemacode.schematics;

import info.teksol.mindcode.AstElement;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.ToolMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.compiler.generator.AbstractMessageEmitter;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.mindcode.mimex.Icons;
import info.teksol.mindcode.v3.InputFiles;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.*;
import info.teksol.schemacode.config.*;
import info.teksol.schemacode.mindustry.*;
import info.teksol.schemacode.schematics.BlockPositionResolver.AstBlockPosition;
import org.intellij.lang.annotations.PrintFormat;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchematicsBuilder extends AbstractMessageEmitter {
    private final InputFiles inputFiles;
    private final CompilerProfile compilerProfile;
    private final AstDefinitions astDefinitions;

    private AstSchematic astSchematic;
    private Map<String, AstText> constants;
    private Map<String, BlockPosition> astLabelMap;
    private BlockPositionMap<BlockPosition> astPositionMap;
    private BlockPositionMap<Block> positionMap;

    public SchematicsBuilder(InputFiles inputFiles, CompilerProfile compilerProfile,
            Consumer<MindcodeMessage> messageConsumer, AstDefinitions astDefinitions) {
        super(messageConsumer);
        this.inputFiles = inputFiles;
        this.compilerProfile = compilerProfile;
        this.astDefinitions = astDefinitions;
    }

    public static SchematicsBuilder create(InputFiles inputFiles, CompilerProfile compilerProfile,
            AstDefinitions definitions, Consumer<MindcodeMessage> messageListener) {
        return new SchematicsBuilder(inputFiles, compilerProfile, messageListener, definitions);
    }

    public void error(@PrintFormat String message, Object... args) {
        addMessage(ToolMessage.error(message, args));
    }

    public void warn(@PrintFormat String message, Object... args) {
        addMessage(ToolMessage.warn(message, args));
    }

    public void info(@PrintFormat String message, Object... args) {
        addMessage(ToolMessage.info(message, args));
    }

    public CompilerProfile getCompilerProfile() {
        return compilerProfile;
    }

    public Path getBasePath() {
        return inputFiles.getBasePath();
    }

    public InputFiles getInputFiles() {
        return inputFiles;
    }

    public boolean externalFilesAllowed() {
        return !getBasePath().toString().isEmpty();
    }

    public Schematic buildSchematics() {
        extractConstants();

        List<AstSchematic> schematicsList = astDefinitions.definitions().stream()
                .filter(AstSchematic.class::isInstance)
                .map(AstSchematic.class::cast)
                .toList();

        if (schematicsList.isEmpty()) {
            addMessage(ToolMessage.error("No schematic defined."));
            return null;
        } else if (schematicsList.size() > 1) {
            addMessage(ToolMessage.error("More than one schematic defined."));
            return null;
        }

        astSchematic = schematicsList.getFirst();

        Map<String, Long> labelCounts = astSchematic.blocks().stream()
                .filter(b -> b.labels() != null && !b.labels().isEmpty())
                .flatMap(b -> b.labels().stream())
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()));

        labelCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .forEachOrdered(c -> addMessage(ToolMessage.error("Multiple definitions of block label '%s'.", c.getKey())));

        astSchematic.blocks().stream().filter(astBlock -> !BlockType.isNameValid(astBlock.type()))
                .forEachOrdered(astBlock -> error(astBlock, "Unknown block type '%s'.", astBlock.type()));

        List<AstBlock> astBlocks = astSchematic.blocks().stream()
                .filter(astBlock -> BlockType.isNameValid(astBlock.type())).toList();

        // Here are absolute positions of all blocks, stored as "#" + index
        // Labeled blocks are additionally stored under all their labels
        BlockPositionResolver positionResolver = new BlockPositionResolver(messageConsumer);
        astLabelMap = positionResolver.resolveAllBlocks(astBlocks);
        List<BlockPosition> blockPositions = astLabelMap.values().stream().distinct().toList();

        astPositionMap = BlockPositionMap.forBuilder(messageConsumer, blockPositions);

        List<Block> blocks = new ArrayList<>();
        for (int index = 0; index < astBlocks.size(); index++) {
            AstBlock astBlock = astBlocks.get(index);
            BlockPosition blockPos = getBlockPosition(index);
            BlockType type = BlockType.forName(astBlock.type());
            Direction direction = astBlock.direction() == null
                    ? Direction.EAST
                    : Direction.valueOf(astBlock.direction().direction().toUpperCase());
            Configuration configuration = convertAstConfiguration(blockPos, astBlock.configuration());
            blocks.add(new Block(astBlock.inputPosition(), index, astBlock.labels(), type, blockPos.position(), direction,
                    configuration.as(ConfigurationType.fromBlockType(type).getBuilderConfigurationClass())));
        }

        String name = getStringAttribute("name", "");
        String description = unwrap(getStringAttribute("description", ""));

        List<String> schemaLabels = getAttributes("label", AstText.class).stream().map(text -> text.getText(this)).toList();
        List<String> additionalLabels = compilerProfile.getAdditionalTags().stream().map(Icons::translateIcon).toList();
        List<String> labels = Stream.concat(schemaLabels.stream(), additionalLabels.stream()).distinct().toList();

        positionMap = BlockPositionMap.forBuilder(m -> {}, blocks);

        blocks = PowerGridSolver.solve(this, blocks);
        BridgeSolver.solve(this, blocks);

        return createSchematic(name, description, labels, blocks);
    }

    private Position calculateOrigin(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return Position.ORIGIN;
        } else {
            int x = blocks.stream().mapToInt(Block::x).min().orElse(0);
            int y = blocks.stream().mapToInt(Block::y).min().orElse(0);
            return new Position(x, y);
        }
    }

    private Position calculateDimensions(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return Position.ORIGIN;
        } else {
            int x = blocks.stream().mapToInt(Block::xMax).max().orElse(0);
            int y = blocks.stream().mapToInt(Block::yMax).max().orElse(0);
            return new Position(x + 1, y + 1);
        }
    }

    private Schematic createSchematic(String name, String description, List<String> labels, List<Block> blocks) {
        // Compensate for non-zero origin
        Position origin = calculateOrigin(blocks);
        List<Block> repositioned = origin.zero() ? blocks : blocks.stream().map(b -> b.remap(p -> p.sub(origin))).toList();
        if (!origin.zero()) {
            info("Schematic origin at (%d, %d) adjusted to (0, 0).", origin.x(), origin.y());
        }

        Position dim = calculateDimensions(repositioned);

        AstCoordinates sourceDim = getAttribute("dimensions", AstCoordinates.class);
        if (sourceDim != null) {
            if (sourceDim.relative()) {
                throw new SchematicsInternalError("Dimensions specified as relative coordinates '%s'.", sourceDim);
            } else if (!sourceDim.coordinates().equals(dim)) {
                if (sourceDim.coordinates().x() < dim.x() || sourceDim.coordinates().y() < dim.y()) {
                    error("Actual schematic dimensions %s are larger than specified dimensions %s.",
                            dim.toStringAbsolute(), sourceDim.coordinates().toStringAbsolute());
                } else {
                    warn("Actual schematic dimensions %s are smaller than specified dimensions %s.",
                            dim.toStringAbsolute(), sourceDim.coordinates().toStringAbsolute());
                }
            }
        }

        Schematic schematic = new Schematic(name, description, labels, dim.x(), dim.y(), repositioned);
        info("Created schematic '%s' with dimensions %s.", name, dim.toStringAbsolute());
        return schematic;
    }

    private void extractConstants() {
        Map<String, List<AstStringConstant>> astConstantLists = astDefinitions.definitions().stream()
                .filter(AstStringConstant.class::isInstance)
                .map(AstStringConstant.class::cast)
                .collect(Collectors.groupingBy(AstStringConstant::name));

        astConstantLists.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream().skip(1))
                .forEachOrdered(node -> error(node, "Identifier '%s' already defined.", node.name()));

        Map<String, AstStringConstant> astConstants = astConstantLists.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFirst()));

        // Resolve indirections
        constants = astConstants.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> resolveConstant(astConstants, new HashSet<>(), e.getValue())));

        // Add all icon constants
        Icons.forEachIcon((k, v) -> constants.put(k, AstStringLiteral.fromText(v.format(null))));
    }

    private AstText resolveConstant(Map<String, AstStringConstant> constantLists, Set<String> visited, AstStringConstant value) {
        AstText text = value.value();
        if (text == null) {
            throw new SchematicsInternalError("Identifier '%s': unexpected null value.", value.name());
        } else if (text instanceof AstStringRef ref) {
            if (!visited.add(ref.reference())) {
                error(ref, "Circular definition of identifier '%s'.", ref.reference());
                return AstStringLiteral.fromText("");
            } else if (!constantLists.containsKey(ref.reference())) {
                error(ref, "Undefined identifier '%s'.", ref.reference());
                return AstStringLiteral.fromText("");
            }
            return resolveConstant(constantLists, visited, constantLists.get(ref.reference()));
        } else {
            return text;
        }
    }

    private Configuration convertAstConfiguration(BlockPosition blockPos, AstConfiguration astConfiguration) {
        Configuration configuration = getConfiguration(blockPos, astConfiguration);

        if (!blockPos.configurationType().isCompatible(configuration)) {
            error(astConfiguration, "Unexpected configuration type for block '%s' at %s: expected %s, found %s.",
                    blockPos.blockType().name(), blockPos.position().toStringAbsolute(),
                    blockPos.configurationType(), ConfigurationType.fromInstance(configuration));
            return EmptyConfiguration.EMPTY; // Ignore wrong configuration but keep processing the block
        } else {
            return configuration;
        }
    }

    private Configuration getConfiguration(BlockPosition blockPos, AstConfiguration astConfiguration) {
        return switch (astConfiguration) {
            case AstBlockReference r        -> verifyConfiguration(r, blockPos, "block");
            case AstBoolean b               -> BooleanConfiguration.of(b.value());
            case AstConnection c            -> c.evaluate(this, blockPos.position());
            case AstConnections c           -> new PositionArray(c.connections().stream().map(p -> p.evaluate(this, blockPos.position())).toList());
            case AstItemReference r         -> verifyConfiguration(r, blockPos, "item");
            case AstLiquidReference r       -> verifyConfiguration(r, blockPos, "liquid");
            case AstProcessor p             -> ProcessorConfiguration.fromAstConfiguration(this, p, blockPos.position());
            case AstRgbaValue rgb           -> convertToRgbValue(blockPos, rgb);
            case AstText t                  -> new TextConfiguration(t.getText(this));
            case AstUnitCommandReference r  -> verifyConfiguration(r, blockPos, "command");
            case AstUnitReference r         -> decodeUnitConfiguration(blockPos, r);
            case null, default              -> EmptyConfiguration.EMPTY;
        };
    }

    private Color convertToRgbValue(BlockPosition blockPos, AstRgbaValue rgb) {
        return new Color(
                clamp(rgb, blockPos, "red", rgb.red()),
                clamp(rgb, blockPos, "green", rgb.green()),
                clamp(rgb, blockPos, "blue", rgb.blue()),
                clamp(rgb, blockPos, "alpha", rgb.alpha())
        );
    }

    private Configuration verifyConfiguration(AstContentsReference reference, BlockPosition blockPos, String valueName) {
        Configuration value = reference.getConfiguration();
        if (value == null) {
            error(reference, "Block '%s' at %s: unknown or unsupported %s '%s'.",
                    blockPos.name(), blockPos.position().toStringAbsolute(), valueName, reference.getConfigurationText());
            return EmptyConfiguration.EMPTY; // Ignore wrong configuration but keep processing the block
        } else {
            return value;
        }
    }

    private int clamp(AstElement element, BlockPosition blockPos, String component, int value) {
        if (value < 0 || value > 255) {
            error(element, "Block '%s' at %s: value %d of color component '%s' outside valid range <0, 255>.",
                    blockPos.name(), blockPos.position().toStringAbsolute(), value, component);
        }
        return Math.max(Math.min(value, 255), 0);
    }

    private Configuration decodeUnitConfiguration(BlockPosition blockPos, AstUnitReference ref) {
        return switch (blockPos.configurationType()) {
            case UNIT_OR_BLOCK -> verifyConfiguration(ref, blockPos, "ref");
            case UNIT_PLAN -> {
                if (blockPos.blockType().unitPlans().contains(ref.unit())) {
                    yield new UnitPlan(ref.unit());
                } else {
                    error(ref, "Block '%s' at %s: unknown or unsupported unit type '%s'.",
                            blockPos.name(), blockPos.position().toStringAbsolute(), ref.unit());
                    yield EmptyConfiguration.EMPTY;
                }
            }
            default -> {
                error(ref, "Block '%s' at %s: unknown or unsupported configuration type '%s'.",
                        blockPos.name(), blockPos.position().toStringAbsolute(), blockPos.configurationType());
                yield EmptyConfiguration.EMPTY;
            }
        };
    }

    private <T> T getAttribute(String name, Class<T> expectedType) {
        List<AstSchemaAttribute> list = astSchematic.attributes().stream().filter(a -> a.attribute().equals(name)).toList();
        if (list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            list.stream().skip(1).forEach(a -> error(a, "Attribute '%s' is already defined.", name));
        }

        if (!expectedType.isInstance(list.getFirst().value())) {
            throw new SchematicsInternalError("Attribute '%s': expected type %s, actual type %s.", name,
                    expectedType, list.getFirst().value().getClass());
        } else {
            return expectedType.cast(list.getFirst().value());
        }
    }

    private <T> List<T> getAttributes(String name, Class<T> expectedType) {
        List<AstSchemaItem> list = astSchematic.attributes().stream()
                .filter(a -> a.attribute().equals(name))
                .map(AstSchemaAttribute::value)
                .toList();
        Optional<AstSchemaItem> wrongType = list.stream().filter(a -> !expectedType.isInstance(a)).findAny();
        if (wrongType.isPresent()) {
            throw new SchematicsInternalError("Attribute '%s': expected type %s, actual type %s.", name,
                    expectedType, wrongType.get().getClass());
        }
        return List.copyOf(list.stream().map(expectedType::cast).toList());
    }

    private String getStringAttribute(String name, String defaultValue) {
        AstText text = getAttribute(name, AstText.class);
        return text == null ? defaultValue : text.getText(this);
    }

    static String unwrap(String text) {
        StringBuilder sbr = new StringBuilder(text.length());
        boolean wasEmpty = false;
        for (String line : text.lines().toList()) {
            if (line.isBlank()) {
                wasEmpty = true;
                continue;
            }
            if (!sbr.isEmpty()) {
                sbr.append(wasEmpty ? '\n' : ' ');
            }
            wasEmpty = false;
            sbr.append(line);
        }

        return sbr.toString();
    }

    public AstText getText(AstElement element, String reference) {
        AstText result = constants.get(reference);
        if (result == null) {
            error(element, "Undefined identifier '%s'.", reference);
            return AstStringLiteral.fromText("");
        }
        return result;
    }

    public BlockPosition getBlockPosition(int index) {
        return requireNonNull(astLabelMap.get("#" + index),
                () -> new SchematicsInternalError("Invalid block index %d.", index));
    }

    public BlockPosition getBlockPosition(AstElement element, String name) {
        BlockPosition blockPosition = astLabelMap.get(name);
        if (blockPosition != null) {
            return blockPosition;
        }
        error(element, "Unknown block label '%s'", name);
        return new AstBlockPosition(0, BlockType.forName("@air"), Position.INVALID);
    }
    public Map<String, BlockPosition> getAstLabelMap() {
        return astLabelMap;
    }


    public BlockPosition getBlockPosition(Position position) {
        return astPositionMap.at(position);
    }

    public Position getAnchor(Position position) {
        BlockPosition anchor = getBlockPosition(position);
        return anchor == null ? position : anchor.position();
    }

    public BlockPositionMap<Block> getPositionMap() {
        return positionMap;
    }

    private static <T, E extends  Throwable> T requireNonNull(T object, Supplier<E> exception) throws E {
        if (object == null) {
            throw exception.get();
        } else {
            return object;
        }
    }

    // Caches result of compiling Mindcode to mlog - avoid repeated recompilation of identical mindcode
    // Maps the entire input string onto the output to avoid obtaining wrong cached version
    private final Map<String, String> compilerCache = new HashMap<>();

    public String getMlogFromCache(String mindcode) {
        return compilerCache.get(mindcode);
    }

    public void storeMlogToCache(String mindcode, String mlog) {
        compilerCache.put(mindcode, mlog);
    }
}
