package info.teksol.schemacode.schema;

import info.teksol.mindcode.compiler.CompilerMessage;
import info.teksol.mindcode.compiler.CompilerProfile;
import info.teksol.mindcode.mimex.Icons;
import info.teksol.schemacode.SchemacodeMessage;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.*;
import info.teksol.schemacode.config.BooleanConfiguration;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.PositionArray;
import info.teksol.schemacode.config.ProcessorConfiguration;
import info.teksol.schemacode.config.TextConfiguration;
import info.teksol.schemacode.mimex.BlockType;
import info.teksol.schemacode.mindustry.ConfigurationType;
import info.teksol.schemacode.mindustry.Direction;
import info.teksol.schemacode.mindustry.Item;
import info.teksol.schemacode.mindustry.Liquid;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.schema.BlockPositionResolver.AstBlockPosition;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: position blocks by lower left corner (??)
// TODO: included file path relative to processed file

public class SchematicsBuilder {
    private final CompilerProfile compilerProfile;
    private final Consumer<CompilerMessage> messageListener;
    private final AstDefinitions astDefinitions;
    private final Path basePath;

    private AstSchematics astSchematics;
    private Map<String, String> constants;
    private Map<String, BlockPosition> astLabelMap;
    private BlockPositionMap<BlockPosition> astPositionMap;
    private BlockPositionMap<Block> positionMap;

    public SchematicsBuilder(CompilerProfile compilerProfile, Consumer<CompilerMessage> messageListener, AstDefinitions astDefinitions, Path basePath) {
        this.compilerProfile = compilerProfile;
        this.messageListener = messageListener;
        this.astDefinitions = astDefinitions;
        this.basePath = basePath;
    }

    public static SchematicsBuilder create(CompilerProfile compilerProfile, AstDefinitions definitions,
            Consumer<CompilerMessage> messageListener, Path basePath) {
        return new SchematicsBuilder(compilerProfile, messageListener, definitions, basePath);
    }

    public void addMessage(CompilerMessage message) {
        messageListener.accept(message);
    }

    public void error(String message, Object... args) {
        messageListener.accept(SchemacodeMessage.error(args.length == 0 ? message : message.formatted(args)));
    }

    public void warn(String message, Object... args) {
        messageListener.accept(SchemacodeMessage.warn(args.length == 0 ? message : message.formatted(args)));
    }

    public void info(String message, Object... args) {
        messageListener.accept(SchemacodeMessage.info(args.length == 0 ? message : message.formatted(args)));
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

    public Schematics buildSchematics() {
        extractConstants();

        List<AstSchematics> schematicsList = astDefinitions.definitions().stream()
                .filter(AstSchematics.class::isInstance)
                .map(AstSchematics.class::cast)
                .toList();

        if (schematicsList.isEmpty()) {
            error("No schematic defined.");
            return null;
        } else if (schematicsList.size() > 1) {
            error("More than one schematic defined.");
            return null;
        }

        astSchematics = schematicsList.get(0);

        Map<String, Long> labelCounts = astSchematics.blocks().stream()
                .filter(b -> b.labels() != null && !b.labels().isEmpty())
                .flatMap(b -> b.labels().stream())
                .collect(Collectors.groupingBy(l -> l, Collectors.counting()));

        labelCounts.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .forEachOrdered(c -> error("Multiple definitions of block label '%s'.", c.getKey()));

        astSchematics.blocks().stream().filter(astBlock -> !BlockType.isNameValid(astBlock.type()))
                .forEachOrdered(astBlock -> error("Unknown block type '%s'.", astBlock.type()));

        List<AstBlock> astBlocks = astSchematics.blocks().stream()
                .filter(astBlock -> BlockType.isNameValid(astBlock.type())).toList();

        // Here are absolute positions of all blocks, stored as "#" + index
        // Labeled blocks are additionally stored under all their labels
        BlockPositionResolver positionResolver = new BlockPositionResolver(messageListener);
        astLabelMap = positionResolver.resolveAllBlocks(astBlocks);
        List<BlockPosition> blockPositions = astLabelMap.values().stream().distinct().toList();

        astPositionMap = BlockPositionMap.forBuilder(messageListener, blockPositions);

        final List<Block> blocks = new ArrayList<>();
        for (int index = 0; index < astBlocks.size(); index++) {
            AstBlock astBlock = astBlocks.get(index);
            BlockPosition blockPos = getBlockPosition(index);
            BlockType type = BlockType.forName(astBlock.type());
            Direction direction = astBlock.direction() == null
                    ? Direction.EAST
                    : Direction.valueOf(astBlock.direction().direction().toUpperCase());
            Configuration configuration = decodeAstConfiguration(blockPos.position(), astBlock.configuration());
            if (!type.configurationType().isCompatible(configuration) && configuration != EmptyConfiguration.EMPTY) {
                error("Unexpected configuration type for block %s at %s: expected %s, found %s.",
                        blockPos.blockType().name(), blockPos.position().toStringAbsolute(),
                        type.configurationType(), ConfigurationType.fromInstance(configuration));
            } else {
                blocks.add(new Block(index, astBlock.labels(), type, blockPos.position(), direction,
                        configuration.as(type.configurationType().getConfigurationClass())));
            }
        }

        String name = getStringAttribute("name", "");
        String description = unwrap(getStringAttribute("description", ""));

        List<String> labels = getAttributes("label", AstText.class).stream().map(text -> text.getText(this)).toList();
        List<String> additionalLabels = compilerProfile.getAdditionalTags().stream().map(Icons::translateIcon).toList();
        List<String> merged = Stream.concat(labels.stream(), additionalLabels.stream()).distinct().toList();

        positionMap = BlockPositionMap.forBuilder(m -> {}, blocks);

        Schematics schematics = new Schematics(name, description, merged, 0, 0, blocks);
        schematics = PowerGridSolver.solve(this, schematics);

        // Compensate for non-zero origin
        Position origin = findLowerLeftCoordinate(schematics.blocks());
        List<Block> repositioned = origin.zero() ? schematics.blocks()
                :  schematics.blocks().stream().map(b -> b.remap(p -> p.sub(origin))).toList();
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

        schematics = new Schematics(schematics.name(), schematics.description(), schematics.labels(), dim.x(), dim.y(), repositioned);
        info("Created schematic '%s' with dimensions (%d, %d).", name, schematics.width(), schematics.height());
        return schematics;
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
        Icons.getIcons().forEach((k, v) -> constants.put(k, v.format()));
    }

    private String resolveConstant(Map<String, AstStringConstant> constantLists, Set<String> visited, AstStringConstant value) {
        return switch (value.value()) {
            case null -> throw new SchematicsInternalError("Identifier '%s': unexpected null value.", value.name());
            case AstStringRef ref -> {
                if (!visited.add(ref.reference())) {
                    error("Circular definition of identifier '%s'.", ref.reference());
                    yield "";
                } else if (!constantLists.containsKey(ref.reference())) {
                    error("Undefined identifier '%s'.", ref.reference());
                    yield "";
                }

                yield resolveConstant(constantLists, visited, constantLists.get(ref.reference()));
            }
            case AstStringBlock block -> block.getValue();
            case AstStringLiteral lit -> lit.getValue();
            default -> throw new SchematicsInternalError("Identifier '%s': unexpected class '%s': %s",
                    value.name(), value.value().getClass(), value.value());
        };
    }

    private Position findLowerLeftCoordinate(List<Block> blocks) {
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

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private Configuration decodeAstConfiguration(Position position, AstConfiguration configuration) {
        return switch (configuration) {
            case null                   -> EmptyConfiguration.EMPTY;
            case AstBoolean b           -> BooleanConfiguration.of(b.value());
            case AstConnection c        -> c.evaluate(this, position);
            case AstConnections c       -> new PositionArray(c.connections().stream().map(p -> p.evaluate(this, position)).toList());
            case AstItemReference r     -> Item.forName(r.item());
            case AstLiquidReference r   -> Liquid.forName(r.item());
            case AstProcessor p         -> ProcessorConfiguration.fromAstConfiguration(this, p, position);
            case AstText t              -> new TextConfiguration(t.getText(this));
            default                     -> EmptyConfiguration.EMPTY;
        };
    }

    private <T> T getAttribute(String name, Class<T> expectedType) {
        List<AstSchemaAttribute> list = astSchematics.attributes().stream().filter(a -> a.attribute().equals(name)).toList();
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
        List<AstSchemaItem> list = astSchematics.attributes().stream()
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
}
