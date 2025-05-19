package info.teksol.mc.mindcode.logic.mimex;

import info.teksol.mc.evaluator.Color;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorVersion;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public class MindustryMetadata {
    private final ProcessorVersion processorVersion;

    private MindustryMetadata(ProcessorVersion processorVersion) {
        this.processorVersion = processorVersion;
    }

    private static final Map<String, MindustryMetadata> cache = new ConcurrentHashMap<>();

    public static MindustryMetadata forVersion(ProcessorVersion processorVersion) {
        return cache.computeIfAbsent(processorVersion.mimexVersion, _ -> new MindustryMetadata(processorVersion));
    }

    private final AtomicReference<Set<String>> stableBuiltins = new AtomicReference<>();
    private final AtomicReference<Icons> icons = new AtomicReference<>();
    private final AtomicReference<Map<String, NamedColor>> colors = new AtomicReference<>();

    private final AtomicReference<Map<String, BlockType>> blockMap = new AtomicReference<>();
    private final AtomicReference<Map<String, Item>> itemMap = new AtomicReference<>();
    private final AtomicReference<Map<String, Liquid>> liquidMap = new AtomicReference<>();
    private final AtomicReference<Map<String, Unit>> unitMap = new AtomicReference<>();
    private final AtomicReference<Map<String, UnitCommand>> unitCommandMap = new AtomicReference<>();
    private final AtomicReference<Map<String, LAccess>> lAccessMap = new AtomicReference<>();
    private final AtomicReference<Map<String, LVar>> lVarMap = new AtomicReference<>();
    private final AtomicReference<Map<String, Weather>> weatherMap = new AtomicReference<>();

    private final AtomicReference<Map<Integer, BlockType>> blockIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, Item>> itemIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, Liquid>> liquidIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, Unit>> unitIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, UnitCommand>> unitCommandIdMap = new AtomicReference<>();

    private final AtomicReference<Map<Integer, BlockType>> blockLogicIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, Item>> itemLogicIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, Liquid>> liquidLogicIdMap = new AtomicReference<>();
    private final AtomicReference<Map<Integer, Unit>> unitLogicIdMap = new AtomicReference<>();

    private final AtomicReference<Map<String, MindustryContent>> allConstants = new AtomicReference<>();

    private final AtomicReference<Set<String>> alignments = new AtomicReference<>();
    private final AtomicReference<Set<String>> blockFlags = new AtomicReference<>();
    private final AtomicReference<Set<String>> tileLayers = new AtomicReference<>();
    private final AtomicReference<Set<String>> lookableContents = new AtomicReference<>();
    private final AtomicReference<Set<String>> markerTypes = new AtomicReference<>();
    private final AtomicReference<Set<String>> radarTargets = new AtomicReference<>();
    private final AtomicReference<Set<String>> radarSorts = new AtomicReference<>();
    private final AtomicReference<Set<String>> lAccessNames = new AtomicReference<>();
    private final AtomicReference<Set<String>> lAccessSettable = new AtomicReference<>();
    private final AtomicReference<Set<String>> tileLayersSettable = new AtomicReference<>();
    private final AtomicReference<Set<String>> soundNames = new AtomicReference<>();
    private final AtomicReference<Set<String>> statusEffects = new AtomicReference<>();

    public static MindustryMetadata getLatest() {
        return forVersion(ProcessorVersion.MAX);
    }

    //<editor-fold desc="Lazy initialization">

    // Lazy initialization via AtomicReference
    private static <T> T cacheInstance(AtomicReference<T> reference, Supplier<T> supplier) {
        T value = reference.get();
        if (value == null) {
            value = supplier.get();
            reference.compareAndSet(null, value);
        }
        return value;
    }

    Set<String> getStableBuiltins() {
        return cacheInstance(stableBuiltins, () -> {
            try (InputStream input = Objects.requireNonNull(BlockTypeReader.class.getResourceAsStream("/mimex/stable-builtins.txt"))) {
                return new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.toCollection(HashSet::new));
            } catch (IOException e) {
                throw new RuntimeException("Cannot read resource /mimex/stable-builtins.txt", e);
            }
        });
    }

    public boolean isStableBuiltin(String name) {
        return getStableBuiltins().contains(name);
    }

    // Icons
    public Icons getIcons() {
        return cacheInstance(icons, () -> new Icons(processorVersion.mimexVersion));
    }

    // Named color
    private Map<String, NamedColor> getNamedColors() {
        return cacheInstance(colors, () -> new NamedColorReader("mimex-colors.txt").createFromResource());
    }

    // Name to instance maps

    Map<String, BlockType> getBlockMap() {
        return cacheInstance(blockMap, () -> new BlockTypeReader("mimex-blocks.txt").createFromResource());
    }

    Map<String, Item> getItemMap() {
        return cacheInstance(itemMap, () -> new SimpleReader<>("mimex-items.txt", Item::new).createFromResource());
    }

    Map<String, Liquid> getLiquidMap() {
        return cacheInstance(liquidMap, () -> new SimpleReader<>("mimex-liquids.txt", Liquid::new).createFromResource());
    }

    Map<String, Unit> getUnitMap() {
        return cacheInstance(unitMap, () -> new SimpleReader<>("mimex-units.txt", Unit::new).createFromResource());
    }

    private Map<String, UnitCommand> getUnitCommandMap() {
        // Not supported in V6
        if (processorVersion == ProcessorVersion.V6) return Map.of();

        return cacheInstance(unitCommandMap, () -> new SimpleReader<>("mimex-commands.txt", UnitCommand::create).createFromResource());
    }

    private Map<String, LAccess> getLAccessMap() {
        return cacheInstance(lAccessMap, () -> new LAccessReader("mimex-laccess.txt").createFromResource());
    }

    Map<String, LVar> getLVarMap() {
        return cacheInstance(lVarMap, () -> new LVarReader("mimex-vars.txt").createFromResource());
    }

    Map<String, Weather> getWeatherMap() {
        return cacheInstance(weatherMap, () -> new SimpleReader<>("mimex-weathers.txt", Weather::new).createFromResource());
    }

    // ID to instance maps

    private <T extends MindustryContent> Map<Integer, T> createIdMap(Map<String, T> map) {
        return map.values().stream().collect(Collectors.toMap(MindustryContent::id, obj -> obj));
    }

    private Map<Integer, BlockType> getBlockIdMap() {
        return cacheInstance(blockIdMap, () -> createIdMap(getBlockMap()));
    }

    private Map<Integer, Item> getItemIdMap() {
        return cacheInstance(itemIdMap, () -> createIdMap(getItemMap()));
    }

    private Map<Integer, Liquid> getLiquidIdMap() {
        return cacheInstance(liquidIdMap, () -> createIdMap(getLiquidMap()));
    }

    private Map<Integer, Unit> getUnitIdMap() {
        return cacheInstance(unitIdMap, () -> createIdMap(getUnitMap()));
    }

    private Map<Integer, UnitCommand> getUnitCommandIdMap() {
        return cacheInstance(unitCommandIdMap, () -> createIdMap(getUnitCommandMap()));
    }

    // LogicId to instance maps

    private <T extends MindustryContent> Map<Integer, T> createLogicIdMap(Map<String, T> map) {
        return map.values().stream().filter(t -> t.logicId() >= 0)
                .collect(Collectors.toMap(MindustryContent::logicId, obj -> obj));
    }

    private Map<Integer, BlockType> getBlockLogicIdMap() {
        return cacheInstance(blockLogicIdMap, () -> createLogicIdMap(getBlockMap()));
    }

    private Map<Integer, Item> getItemLogicIdMap() {
        return cacheInstance(itemLogicIdMap, () -> createLogicIdMap(getItemMap()));
    }

    private Map<Integer, Liquid> getLiquidLogicIdMap() {
        return cacheInstance(liquidLogicIdMap, () -> createLogicIdMap(getLiquidMap()));
    }

    private Map<Integer, Unit> getUnitLogicIdMap() {
        return cacheInstance(unitLogicIdMap, () -> createLogicIdMap(getUnitMap()));
    }

    // All constants
    private Map<String, MindustryContent> getAllConstants() {
        // Note: in version 6, the block map contains entries conflicting with other content (e.g. water)
        // The block map is processed last and any conflicting items are discarded
        return cacheInstance(allConstants, () ->
                Stream.of(getItemMap(), getLiquidMap(), getUnitMap(), getUnitCommandMap(), getBlockMap())
                        .flatMap(m -> m.values().stream())
                        .collect(Collectors.toMap(MindustryContent::name, t -> t,
                                (a, b) -> a, HashMap::new)));
    }

    // Alignments
    public Set<String> getAlignments() {
        return cacheInstance(alignments, () -> new NamedReader("mimex-alignments.txt").createFromResource());
    }

    public Set<String> getBlockFlags() {
        return cacheInstance(blockFlags, () -> new NamedReader("mimex-block-flags.txt",
                "logic", "true"::equalsIgnoreCase).createFromResource());
    }

    public Set<String> getTileLayers() {
        return cacheInstance(tileLayers, () -> new NamedReader("mimex-layers.txt").createFromResource());
    }

    public Set<String> getLookableContents() {
        return cacheInstance(lookableContents, () -> new NamedReader("mimex-contents.txt",
                "lookable", "true"::equalsIgnoreCase).createFromResource());
    }

    public Set<String> getMarkerTypes() {
        return cacheInstance(markerTypes, () -> new NamedReader("mimex-markers.txt").createFromResource());
    }

    public Set<String> getRadarTargets() {
        return cacheInstance(radarTargets, () -> new NamedReader("mimex-radar-targets.txt").createFromResource());
    }

    public Set<String> getRadarSorts() {
        return cacheInstance(radarSorts, () -> new NamedReader("mimex-radar-sorts.txt").createFromResource());
    }

    public Set<String> getLAccessNames() {
        return cacheInstance(lAccessNames, () -> getLAccessMap().values().stream()
                .filter(LAccess::senseable)
                .map(LAccess::name)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public Set<String> getLAccessSettableNames() {
        return cacheInstance(lAccessSettable, () -> getLAccessMap().values().stream()
                .filter(LAccess::settable)
                .map(LAccess::name)
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public Set<String> getTileLayersSettable() {
        return cacheInstance(tileLayersSettable, () -> new NamedReader("mimex-layers.txt",
                "settable", "true"::equalsIgnoreCase).createFromResource());
    }

    public Set<String> getSoundNames() {
        return cacheInstance(soundNames, () -> new NamedReader("mimex-sounds.txt", s -> "@sfx-" + s).createFromResource());
    }

    public Set<String> getStatusEffects() {
        return cacheInstance(statusEffects, () -> new NamedReader("mimex-status-effects.txt",
                "hidden", "false"::equalsIgnoreCase).createFromResource());
    }

    public Set<String> getUnitTypes() {
        return getUnitMap().keySet();
    }

    public Set<String> getWeathers() {
        return getWeatherMap().keySet();
    }
    //</editor-fold>

    //<editor-fold desc="All content">
    public @Nullable MindustryContent getNamedContent(String name) {
        return getAllConstants().get(name);
    }

    public @Nullable Map<Integer, ? extends MindustryContent> getLookupMap(String type) {
        return switch (type) {
            case "block" -> getBlockLogicIdMap();
            case "liquid" -> getLiquidLogicIdMap();
            case "item" -> getItemLogicIdMap();
            case "unit" -> getUnitLogicIdMap();
            default -> null;
        };
    }
    //</editor-fold>

    //<editor-fold desc="Block types">
    public int getBlockCount() {
        return getBlockLogicIdMap().size();
    }

    public boolean isBlockNameValid(String name) {
        return getBlockMap().containsKey(name);
    }

    public BlockType getExistingBlock(String name) {
        return Objects.requireNonNull(getBlockMap().get(name));
    }

    public @Nullable BlockType getBlockByName(String name) {
        return getBlockMap().get(name);
    }

    public @Nullable BlockType getBlockById(int id) {
        return getBlockIdMap().get(id);
    }
    //</editor-fold>

    //<editor-fold desc="Colors">
    public boolean isValidColorName(String name) {
        return getNamedColors().containsKey(name);
    }

    public @Nullable NamedColor getColorByName(String name) {
        return getNamedColors().get(name);
    }
    //</editor-fold>

    //<editor-fold desc="Items">
    public int getItemCount() {
        return getItemLogicIdMap().size();
    }

    public @Nullable Item getItemByName(String name) {
        return getItemMap().get(name);
    }

    public @Nullable Item getItemById(int id) {
        return getItemIdMap().get(id);
    }

    public Set<String> getItemNames() {
        return getItemMap().keySet();
    }
    //</editor-fold>
    
    //<editor-fold desc="Liquids">
    public int getLiquidCount() {
        return getLiquidLogicIdMap().size();
    }

    public @Nullable Liquid getLiquidByName(String name) {
        return getLiquidMap().get(name);
    }

    public @Nullable Liquid getLiquidById(int id) {
        return getLiquidIdMap().get(id);
    }
    //</editor-fold>
    
    //<editor-fold desc="Units">
    public int getUnitCount() {
        return getUnitLogicIdMap().size();
    }

    public @Nullable Unit getUnitByName(String name) {
        return getUnitMap().get(name);
    }

    public @Nullable Unit getUnitById(int id) {
        return getUnitIdMap().get(id);
    }
    //</editor-fold>

    //<editor-fold desc="UnitCommands">
    public @Nullable UnitCommand getUnitCommandByName(String name) {
        return getUnitCommandMap().get(name);
    }

    public @Nullable UnitCommand getUnitCommandById(int id) {
        return getUnitCommandIdMap().get(id);
    }
    //</editor-fold>

    //<editor-fold desc="Vars">
    public boolean isBuiltInValid(String name) {
        return getLVarMap().containsKey(name);
    }

    public Collection<LVar> getAllLVars() {
        return getLVarMap().values();
    }

    public @Nullable LVar getLVar(String name) {
        return getLVarMap().get(name);
    }
    //</editor-fold>

    //<editor-fold desc="Metadata loading">
    private abstract class AbstractReader {
        protected final String resourceName;
        protected final List<String> lines;
        protected final List<String> header;

        protected abstract void parseHeader();

        protected AbstractReader(String resource) {
            this.resourceName = "/mimex/" + processorVersion.mimexVersion + "/" + resource;

            try (InputStream input = MindustryMetadata.class.getResourceAsStream(resourceName)) {
                if (input == null) {
                    lines = List.of();
                    header = List.of();
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    lines = reader.lines()
                            .filter(l -> !l.startsWith("//") && !l.isBlank())
                            .toList();
                    header = List.of(lines.getFirst().split(";"));
                    parseHeader();
                }
            } catch (IOException e) {
                throw new RuntimeException("Cannot read resource " + resourceName, e);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing file " + resourceName, e);
            }
        }

        protected int findColumn(String columnName) {
            int index = header.indexOf(columnName);
            if (index < 0 && !header.isEmpty()) {
                throw new IllegalStateException("Cannot locate column " + columnName + " in " + lines.getFirst());
            }
            return index;
        }
    }

    private abstract class AbstractContentReader<T extends NamedContent> extends  AbstractReader {
        protected final int logicIdIndex;

        protected abstract @Nullable T create(String[] columns);

        protected List<T> createUnregistered() {
            return List.of();
        }

        public AbstractContentReader(String resource) {
            super(resource);
            logicIdIndex = header.indexOf("logicId");
        }

        protected int parseLogicId(String[] columns) {
            if (logicIdIndex < 0) return -1;
            return Integer.parseInt(columns[logicIdIndex]);
        }

        public Map<String, T> createFromResource() {
            try {
                ArrayList<@Nullable T> list = new ArrayList<>();
                for (int i = 1; i < lines.size(); i++) {
                    String[] columns = lines.get(i).split(";", -1);
                    list.add(create(columns));
                }
                Map<String, T> result = list.stream().filter(Objects::nonNull).collect(Collectors.toMap(T::name,
                        t -> t, (a, b) -> a, LinkedHashMap::new));
                createUnregistered().forEach(t -> result.putIfAbsent(t.name(), t));

                return result;
            } catch (Exception e) {
                throw new RuntimeException("Error parsing file " + resourceName, e);
            }
        }
    }

    private class SimpleReader<T extends MindustryContent> extends AbstractContentReader<T> {
        private interface ItemConstructor<T extends MindustryContent> {
            T construct(String contentName, String name, int id, int logicId);
        }

        private final SimpleReader.ItemConstructor<T> itemConstructor;
        private int name, id;

        public SimpleReader(String resource, SimpleReader.ItemConstructor<T> itemConstructor) {
            super(resource);
            this.itemConstructor = itemConstructor;
        }

        @Override
        protected void parseHeader() {
            name = findColumn("name");
            id = findColumn("id");
        }

        @Override
        protected T create(String[] columns) {
            return itemConstructor.construct(
                    columns[name],
                    "@" + columns[name],
                    Integer.parseInt(columns[id]),
                    parseLogicId(columns));
        }
    }

    private class BlockTypeReader extends AbstractContentReader<BlockType> {
        private int name, id, visibility, implementation, size, hasPower, configurable, category, range, maxNodes, rotate, unitPlans;

        public BlockTypeReader(String resource) {
            super(resource);
        }

        @Override
        protected void parseHeader() {
            name = findColumn("name");
            id = findColumn("id");
            visibility = findColumn("visibility");
            implementation = findColumn("subclass");
            size = findColumn("size");
            hasPower = findColumn("hasPower");
            configurable = findColumn("configurable");
            category = findColumn("category");
            maxNodes = findColumn("maxNodes");
            range = findColumn("range");
            rotate = findColumn("rotate");
            unitPlans = findColumn("unitPlans");
        }

        @Override
        protected BlockType create(String[] columns) {
            return new BlockType(
                    columns[name],
                    "@" + columns[name],
                    Integer.parseInt(columns[id]),
                    parseLogicId(columns),
                    columns[visibility],
                    columns[implementation].toUpperCase(),
                    Integer.parseInt(columns[size]),
                    Boolean.parseBoolean(columns[hasPower]),
                    Boolean.parseBoolean(columns[configurable]),
                    (columns[category]),
                    Float.parseFloat(columns[range]),
                    Integer.parseInt(columns[maxNodes]),
                    Boolean.parseBoolean(columns[rotate]),
                    parseUnitPlans(columns[unitPlans]));
        }

        private List<String> parseUnitPlans(String unitPlans) {
            return unitPlans.isBlank() ? List.of() : Stream.of(unitPlans.split("\\|")).map("@"::concat).toList();
        }
    }

    private class NamedColorReader extends AbstractContentReader<NamedColor> {
        private int name, rgba;

        public NamedColorReader(String resource) {
            super(resource);
        }

        @Override
        protected void parseHeader() {
            name = findColumn("name");
            rgba = findColumn("rgba");
        }

        @Override
        protected NamedColor create(String[] columns) {
            return new NamedColor(
                    columns[name],
                    Color.parseColor(columns[rgba]));
        }
    }

    private class LAccessReader extends AbstractContentReader<LAccess> {
        private int name, senseable, controls, settable, parameters;

        public LAccessReader(String resource) {
            super(resource);
        }

        @Override
        protected void parseHeader() {
            name = findColumn("name");
            senseable = findColumn("senseable");
            controls = findColumn("controls");
            settable = findColumn("settable");
            parameters = findColumn("parameters");
        }

        @Override
        protected LAccess create(String[] columns) {
            return new LAccess(
                    columns[name],
                    "@" + columns[name],
                    Boolean.parseBoolean(columns[senseable]),
                    Boolean.parseBoolean(columns[controls]),
                    Boolean.parseBoolean(columns[settable]),
                    columns[parameters]);
        }
    }

    private class LVarReader extends AbstractContentReader<LVar> {
        private int name, global, isobj, constant, numval;

        public LVarReader(String resource) {
            super(resource);
        }

        @Override
        protected List<LVar> createUnregistered() {
            return List.of(
                    new LVar("links", "@links", false, false, false, 0.0),
                    new LVar("mapw", "@mapw", false, false, false, 0.0),
                    new LVar("maph", "@maph", false, false, false, 0.0),
                    new LVar("wait", "@wait", false, false, false, 0.0),
                    new LVar("thisx", "@thisx", false, false, false, 0.0),
                    new LVar("thisy", "@thisy", false, false, false, 0.0)
            );
        }

        @Override
        protected void parseHeader() {
            name = findColumn("name");
            global = findColumn("global");
            isobj = findColumn("isobj");
            constant = findColumn("constant");
            numval = findColumn("numval");
        }

        @Override
        protected @Nullable LVar create(String[] columns) {
            return columns[name].charAt(0) == '@' ? new LVar(
                    columns[name].substring(1),
                    columns[name],
                    "global".equals(columns[global]),
                    Boolean.parseBoolean(columns[isobj]),
                    Boolean.parseBoolean(columns[constant]),
                    Double.parseDouble(columns[numval]))
                    : null;
        }
    }

    private class NamedReader extends AbstractReader {
        private final int filterColumn;
        private final Predicate<String> filter;
        private final Function<String, String> decorator;
        private int name;

        public NamedReader(String resource) {
            super(resource);
            filterColumn = 0;
            filter = s -> true;
            decorator = s -> s;
        }

        public NamedReader(String resource, String filterColumn, Predicate<String> filter) {
            super(resource);
            this.filterColumn = header.indexOf(filterColumn);
            this.filter = filter;
            this.decorator = s -> s;
            if (this.filterColumn < 0 && !header.isEmpty()) {
                throw new IllegalArgumentException("Cannot find column " + filterColumn + " in " + lines.getFirst());
            }
        }

        public NamedReader(String resource, Function<String, String> decorator) {
            super(resource);
            this.filterColumn = 0;
            this.filter = s -> true;
            this.decorator = decorator;
        }

        protected void parseHeader() {
            name = findColumn("name");
        }

        protected boolean matches(String[] columns) {
            return filter.test(columns[filterColumn]);
        }

        public Set<String> createFromResource() {
            try {
                return lines.stream().skip(1)
                        .map(l -> l.split(";", -1))
                        .filter(this::matches)
                        .map(columns -> decorator.apply(columns[name]))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
            } catch (Exception e) {
                throw new RuntimeException("Error parsing file " + resourceName, e);
            }
        }
    }
    //</editor-fold>
}
