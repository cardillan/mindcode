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
import info.teksol.schemacode.schema.BlockPositionResolver.BlockPosition;

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
    private final AstSchematics astSchematics;
    private final Map<String, String> constants;
    private final Map<String, BlockPosition> blockNameMap;
    private final Map<Position, BlockPosition> blockPositionMap;
    private final Consumer<CompilerMessage> messageListener;
    private final Path basePath;

    private SchematicsBuilder(CompilerProfile compilerProfile, AstSchematics astSchematics, Map<String, String> constants,
            Map<String, BlockPosition> blockNameMap, Map<Position, BlockPosition> blockPositionMap,
            Consumer<CompilerMessage> messageListener, Path basePath) {
        this.compilerProfile = compilerProfile;
        this.astSchematics = astSchematics;
        this.constants = constants;
        this.blockNameMap = blockNameMap;
        this.blockPositionMap = blockPositionMap;
        this.messageListener = messageListener;
        this.basePath = basePath;
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

    public static SchematicsBuilder create(CompilerProfile compilerProfile, AstDefinitions definitions,
            Consumer<CompilerMessage> messageListener, Path basePath) {
        List<AstSchematics> schematicsList = definitions.definitions().stream()
                .filter(AstSchematics.class::isInstance)
                .map(AstSchematics.class::cast)
                .toList();

        if (schematicsList.isEmpty()) {
            messageListener.accept(SchemacodeMessage.error("No schematic defined."));
            return null;
        } else if (schematicsList.size() > 1) {
            messageListener.accept(SchemacodeMessage.error("More than one schematic defined."));
            return null;
        }

        AstSchematics astSchematics = schematicsList.get(0);

        Map<String, List<AstStringConstant>> constantLists = definitions.definitions().stream()
                .filter(AstStringConstant.class::isInstance)
                .map(AstStringConstant.class::cast)
                .collect(Collectors.groupingBy(AstStringConstant::name));

        List<String> redefinition = constantLists.entrySet().stream()
                .filter(e -> e.getValue().size() > 1).map(Map.Entry::getKey).toList();
        if (!redefinition.isEmpty()) {
            redefinition.forEach(id -> messageListener.accept(SchemacodeMessage.error("Identifier '" + id + "' defined more than once.")));
            return null;
        }

        // Resolve indirections
        Map<String, String> constants = constantLists.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> resolve(constantLists, new HashSet<>(), e.getValue().get(0), messageListener)));

        // Add all icon constants
        Icons.getIcons().forEach((k, v) -> constants.put(k, v.format()));

        // Here are absolute positions of all blocks, stored as "#" + index
        // Labeled blocks are additionally stored under all their labels
        BlockPositionResolver positionResolver = new BlockPositionResolver(messageListener);
        Map<String, BlockPosition> blockNameMap = positionResolver.resolveAllBlocks(astSchematics);
        List<BlockPosition> blockPositions = blockNameMap.values().stream().distinct().toList();

        Map<Position, List<BlockPosition>> byPosition = blockPositions.stream().collect(Collectors.groupingBy(BlockPosition::position));
        List<Position> collisions = byPosition.values().stream().filter(l -> l.size() > 1).map(l -> l.get(0).position()).toList();
        if (!collisions.isEmpty()) {
            collisions.forEach(c -> messageListener.accept(SchemacodeMessage.error("Multiple blocks at position "
                                                                                   + c.toStringAbsolute() + ".")));
            return null;
        }

        Map<Position, BlockPosition> blockPositionMap = blockPositions.stream()
                .collect(Collectors.toMap(BlockPosition::position, b -> b));

        return new SchematicsBuilder(compilerProfile, astSchematics, constants, blockNameMap, blockPositionMap,
                messageListener, basePath);
    }

    private static String resolve(Map<String, List<AstStringConstant>> constantLists, Set<String> visited, AstStringConstant value,
            Consumer<CompilerMessage> messageListener) {
        return switch (value.value()) {
            case null -> throw new SchematicsInternalError("Identifier '%s': unexpected null value.", value.name());
            case AstStringRef ref -> {
                if (!visited.add(ref.reference())) {
                    messageListener.accept(SchemacodeMessage.error("Circular definition of identifier '" + ref.reference() + "'."));
                    yield "";
                } else if (!constantLists.containsKey(ref.reference())) {
                    messageListener.accept(SchemacodeMessage.error("Undefined identifier '" + ref.reference() + "'."));
                    yield "";
                }

                yield resolve(constantLists, visited, constantLists.get(ref.reference()).get(0), messageListener);
            }
            case AstStringBlock block -> block.getValue();
            case AstStringLiteral lit -> lit.getValue();
            default -> throw new SchematicsInternalError("Identifier '%s': unexpected class '%s': %s",
                    value.name(), value.value().getClass(), value.value());
        };
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
        final List<Block> blocks = new ArrayList<>();
        for (int index = 0; index < astSchematics.blocks().size(); index++) {
            AstBlock astBlock = astSchematics.blocks().get(index);
            BlockPosition blockPos = getBlock(index);
            BlockType type = BlockType.forName(astBlock.type());
            if (type == null) {
                error("Unknown block type '%s'.", astBlock.type());
                continue;
            }
            Direction direction = astBlock.direction() == null
                    ? Direction.EAST
                    : Direction.valueOf(astBlock.direction().direction().toUpperCase());
            Configuration configuration = decodeAstConfiguration(blockPos.position(), astBlock.configuration());
            if (!type.configurationType().isCompatible(configuration) && configuration != EmptyConfiguration.EMPTY) {
                error("Unexpected configuration type for block %s at %s: expected %s, found %s.",
                        blockPos.blockType().name(), blockPos.position().toStringAbsolute(),
                        type.configurationType(), ConfigurationType.fromInstance(configuration));
            } else {
                blocks.add(new Block(astBlock.labels(), type, blockPos.position(), direction,
                        configuration.as(type.configurationType().getConfigurationClass())));
            }
        }

        String name = getStringAttribute("name", "");
        String description = unwrap(getStringAttribute("description", ""));

        Position dim = calculateDimensions(blocks);
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
                    dim = sourceDim.coordinates();
                }
            }
        }

        List<String> labels = getAttributes("label", AstText.class).stream().map(text -> text.getText(this)).toList();
        List<String> additionalLabels = compilerProfile.getAdditionalTags().stream().map(Icons::translateIcon).toList();
        List<String> merged = Stream.concat(labels.stream(), additionalLabels.stream()).distinct().toList();

        info("Created schematic '%s' with dimensions (%d, %d).", name, dim.x(), dim.y());

        return new Schematics(name, description, merged, dim.x(), dim.y(), blocks);
    }

    private Position calculateDimensions(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return Position.ORIGIN;
        } else {
            int x = blocks.stream().mapToInt(Block::xMax).max().getAsInt();
            int y = blocks.stream().mapToInt(Block::yMax).max().getAsInt();
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

    public BlockPosition getBlock(String name) {
        BlockPosition blockPosition = blockNameMap.get(name);
        if (blockPosition != null) {
            return blockPosition;
        }
        error("Unknown block name '%s'", name);
        return new BlockPosition(0, BlockType.forName("@air"), Position.INVALID);
    }

    public BlockPosition getBlock(int index) {
        return requireNonNull(blockNameMap.get("#" + index),
                () -> new SchematicsInternalError("Invalid block index %d.", index));
    }

    public BlockPosition getBlock(Position position) {
        return blockPositionMap.get(position);
    }

    public Map<String, BlockPosition> getBlockNameMap() {
        return blockNameMap;
    }

    private static <T, E extends  Throwable> T requireNonNull(T object, Supplier<E> exception) throws E {
        if (object == null) {
            throw exception.get();
        } else {
            return object;
        }
    }
}
