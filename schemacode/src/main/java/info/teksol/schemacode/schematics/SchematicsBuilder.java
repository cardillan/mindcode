package info.teksol.schemacode.schematics;

import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.mimex.BlockType;
import info.teksol.mindcode.mimex.Icons;
import info.teksol.schemacode.SchemacodeCompilerMessage;
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

public class SchematicsBuilder {
    private final CompilerProfile compilerProfile;
    private final Consumer<MindcodeMessage> messageListener;
    private final AstDefinitions astDefinitions;
    private final Path basePath;

    private AstSchematic astSchematic;
    private Map<String, String> constants;
    private Map<String, BlockPosition> astLabelMap;
    private BlockPositionMap<BlockPosition> astPositionMap;
    private BlockPositionMap<Block> positionMap;

    public SchematicsBuilder(CompilerProfile compilerProfile, Consumer<MindcodeMessage> messageListener, AstDefinitions astDefinitions, Path basePath) {
        this.compilerProfile = compilerProfile;
        this.messageListener = messageListener;
        this.astDefinitions = astDefinitions;
        this.basePath = basePath;
    }

    public static SchematicsBuilder create(CompilerProfile compilerProfile, AstDefinitions definitions,
            Consumer<MindcodeMessage> messageListener, Path basePath) {
        return new SchematicsBuilder(compilerProfile, messageListener, definitions, basePath);
    }

    public void addMessage(MindcodeMessage message) {
        messageListener.accept(message);
    }

    public void error(@PrintFormat String message, Object... args) {
        messageListener.accept(SchemacodeCompilerMessage.error(args.length == 0 ? message : message.formatted(args)));
    }

    public void warn(@PrintFormat String message, Object... args) {
        messageListener.accept(SchemacodeCompilerMessage.warn(args.length == 0 ? message : message.formatted(args)));
    }

    public void info(@PrintFormat String message, Object... args) {
        messageListener.accept(SchemacodeCompilerMessage.info(args.length == 0 ? message : message.formatted(args)));
    }

    public CompilerProfile getCompilerProfile() {
        return compilerProfile;
    }

    public Path getBasePath() {
        return basePath;
    }

    public boolean externalFilesAllowed() {
        return basePath != null;
    }

    public Schematic buildSchematics() {
        extractConstants();

        List<AstSchematic> schematicsList = astDefinitions.definitions().stream()
                .filter(AstSchematic.class::isInstance)
                .map(AstSchematic.class::cast)
                .toList();

        if (schematicsList.isEmpty()) {
            error("No schematic defined.");
            return null;
        } else if (schematicsList.size() > 1) {
            error("More than one schematic defined.");
            return null;
        }

        astSchematic = schematicsList.get(0);

        Map<String, Long> labelCounts = astSchematic.blocks().stream()
                .filter(b -> b.labels() != null && !b.labels().isEmpty())
                .flatMap(b -> b.labels().stream())
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()));

        labelCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .forEachOrdered(c -> error("Multiple definitions of block label '%s'.", c.getKey()));

        astSchematic.blocks().stream().filter(astBlock -> !BlockType.isNameValid(astBlock.type()))
                .forEachOrdered(astBlock -> error("Unknown block type '%s'.", astBlock.type()));

        List<AstBlock> astBlocks = astSchematic.blocks().stream()
                .filter(astBlock -> BlockType.isNameValid(astBlock.type())).toList();

        // Here are absolute positions of all blocks, stored as "#" + index
        // Labeled blocks are additionally stored under all their labels
        BlockPositionResolver positionResolver = new BlockPositionResolver(messageListener);
        astLabelMap = positionResolver.resolveAllBlocks(astBlocks);
        List<BlockPosition> blockPositions = astLabelMap.values().stream().distinct().toList();

        astPositionMap = BlockPositionMap.forBuilder(messageListener, blockPositions);

        List<Block> blocks = new ArrayList<>();
        for (int index = 0; index < astBlocks.size(); index++) {
            AstBlock astBlock = astBlocks.get(index);
            BlockPosition blockPos = getBlockPosition(index);
            BlockType type = BlockType.forName(astBlock.type());
            Direction direction = astBlock.direction() == null
                    ? Direction.EAST
                    : Direction.valueOf(astBlock.direction().direction().toUpperCase());
            Configuration configuration = convertAstConfiguration(blockPos, astBlock.configuration());
            blocks.add(new Block(index, astBlock.labels(), type, blockPos.position(), direction,
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

        List<String> redefinition = astConstantLists.entrySet().stream()
                .filter(e -> e.getValue().size() > 1).map(Map.Entry::getKey).toList();
        if (!redefinition.isEmpty()) {
            redefinition.forEach(id -> error("Identifier '%s' defined more than once.", id));
        }

        Map<String, AstStringConstant> astConstants = astConstantLists.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        // Resolve indirections
        constants = astConstants.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> resolveConstant(astConstants, new HashSet<>(), e.getValue())));

        // Add all icon constants
        Icons.forEachIcon((k, v) -> constants.put(k, v.format()));
    }

    private String resolveConstant(Map<String, AstStringConstant> constantLists, Set<String> visited, AstStringConstant value) {
        AstText ast = value.value();
        if (ast == null) {
            throw new SchematicsInternalError("Identifier '%s': unexpected null value.", value.name());
        } else if (ast instanceof AstStringRef ref) {
            if (!visited.add(ref.reference())) {
                error("Circular definition of identifier '%s'.", ref.reference());
                return "";
            } else if (!constantLists.containsKey(ref.reference())) {
                error("Undefined identifier '%s'.", ref.reference());
                return "";
            }
            return resolveConstant(constantLists, visited, constantLists.get(ref.reference()));
        } else if (ast instanceof AstStringBlock block) {
            return block.getValue();
        } else if (ast instanceof AstStringLiteral lit) {
            return lit.getValue();
        } else {
            throw new SchematicsInternalError("Identifier '%s': unexpected class '%s': %s", value.name(), ast.getClass(), ast);
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private Configuration convertAstConfiguration(BlockPosition blockPos, AstConfiguration astConfiguration) {
        Configuration configuration = getConfiguration(blockPos, astConfiguration);

        if (!blockPos.configurationType().isCompatible(configuration)) {
            error("Unexpected configuration type for block '%s' at %s: expected %s, found %s.",
                    blockPos.blockType().varName(), blockPos.position().toStringAbsolute(),
                    blockPos.configurationType(), ConfigurationType.fromInstance(configuration));
            return EmptyConfiguration.EMPTY; // Ignore wrong configuration but keep processing the block
        } else {
            return configuration;
        }
    }

    private Configuration getConfiguration(BlockPosition blockPos, AstConfiguration astConfiguration) {
        if (astConfiguration == null)                                 return  EmptyConfiguration.EMPTY;
        if (astConfiguration instanceof AstBlockReference r)          return  verifyValue(blockPos, BlockConfiguration.forName(r.item()), r.item(), "block");
        if (astConfiguration instanceof AstBoolean b)                 return  BooleanConfiguration.of(b.value());
        if (astConfiguration instanceof AstConnection c)              return  c.evaluate(this, blockPos.position());
        if (astConfiguration instanceof AstConnections c)             return  new PositionArray(c.connections().stream().map(p -> p.evaluate(this, blockPos.position())).toList());
        if (astConfiguration instanceof AstItemReference r)           return  verifyValue(blockPos, ItemConfiguration.forName(r.item()), r.item(), "item");
        if (astConfiguration instanceof AstLiquidReference r)         return  verifyValue(blockPos, LiquidConfiguration.forName(r.liquid()), r.liquid(), "liquid");
        if (astConfiguration instanceof AstProcessor p)               return  ProcessorConfiguration.fromAstConfiguration(this, p, blockPos.position());
        if (astConfiguration instanceof AstRgbaValue rgb)             return  convertToRgbValue(blockPos, rgb);
        if (astConfiguration instanceof AstText t)                    return  new TextConfiguration(t.getText(this));
        if (astConfiguration instanceof AstUnitCommandReference r)    return  verifyValue(blockPos, UnitCommandConfiguration.forName(r.item()), r.item(), "command");
        if (astConfiguration instanceof AstUnitReference r)           return  decodeUnitConfiguration(blockPos, r);
        return EmptyConfiguration.EMPTY;
    }

    private Color convertToRgbValue(BlockPosition blockPos, AstRgbaValue rgb) {
        return new Color(
                clamp(blockPos, "red", rgb.red()),
                clamp(blockPos, "green", rgb.green()),
                clamp(blockPos, "blue", rgb.blue()),
                clamp(blockPos, "alpha", rgb.alpha())
        );
    }

    private Configuration verifyValue(BlockPosition blockPos, Configuration value, String strValue, String valueName) {
        if (value == null) {
            error("Block '%s' at %s: unknown or unsupported %s '%s'.",
                    blockPos.name(), blockPos.position().toStringAbsolute(), valueName, strValue);
            return EmptyConfiguration.EMPTY; // Ignore wrong configuration but keep processing the block
        } else {
            return value;
        }
    }

    private int clamp(BlockPosition blockPos, String component, int value) {
        if (value < 0 || value > 255) {
            error("Block '%s' at %s: value %d of color component '%s' outside valid range <0, 255>.",
                    blockPos.name(), blockPos.position().toStringAbsolute(), value, component);
        }
        return Math.max(Math.min(value, 255), 0);
    }

    private Configuration decodeUnitConfiguration(BlockPosition blockPos, AstUnitReference ref) {
        return switch (blockPos.configurationType()) {
            case UNIT_OR_BLOCK -> verifyValue(blockPos, UnitConfiguration.forName(ref.unit()), ref.unit(), "ref");
            case UNIT_PLAN -> {
                if (blockPos.blockType().unitPlans().contains(ref.unit())) {
                    yield new UnitPlan(ref.unit());
                } else {
                    error("Block '%s' at %s: unknown or unsupported unit type '%s'.",
                            blockPos.name(), blockPos.position().toStringAbsolute(), ref.unit());
                    yield EmptyConfiguration.EMPTY;
                }
            }
            default -> {
                error("Block '%s' at %s: unknown or unsupported configuration type '%s'.",
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
            error("Multiple definitions of attribute '%s'.", name);
        }

        if (!expectedType.isInstance(list.get(0).value())) {
            throw new SchematicsInternalError("Attribute '%s': expected type %s, actual type %s.", name,
                    expectedType, list.get(0).value().getClass());
        } else {
            return expectedType.cast(list.get(0).value());
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
            if (sbr.length() > 0) {
                sbr.append(wasEmpty ? '\n' : ' ');
            }
            wasEmpty = false;
            sbr.append(line);
        }

        return sbr.toString();
    }

    public String getText(String reference) {
        String result = constants.get(reference);
        if (result == null) {
            error("Undefined identifier '%s'.", reference);
            return "";
        }
        return result;
    }

    public BlockPosition getBlockPosition(int index) {
        return requireNonNull(astLabelMap.get("#" + index),
                () -> new SchematicsInternalError("Invalid block index %d.", index));
    }

    public BlockPosition getBlockPosition(String name) {
        BlockPosition blockPosition = astLabelMap.get(name);
        if (blockPosition != null) {
            return blockPosition;
        }
        error("Unknown block label '%s'", name);
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

    // Caches result of compiling Mindcode to mlog - avid repeated recompilation of identical mindcode
    private final Map<String, String> compilerCache = new HashMap<>();

    public String getMlogFromCache(String mindcode) {
        return compilerCache.get(mindcode);
    }

    public void storeMlogToCache(String mindcode, String mlog) {
        compilerCache.put(mindcode, mlog);
    }
}
