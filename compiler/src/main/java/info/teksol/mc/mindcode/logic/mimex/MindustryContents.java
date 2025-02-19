package info.teksol.mc.mindcode.logic.mimex;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@NullMarked
public class MindustryContents {
    static final Map<String, BlockType> BLOCK_MAP = new BlockTypeReader("mimex-blocks.txt").createFromResource();
    static final Map<String, Item> ITEM_MAP = new SimpleReader<>("mimex-items.txt", Item::new).createFromResource();
    static final Map<String, Liquid> LIQUID_MAP = new SimpleReader<>("mimex-liquids.txt", Liquid::new).createFromResource();
    static final Map<String, Unit> UNIT_MAP = new SimpleReader<>("mimex-units.txt", Unit::new).createFromResource();
    static final Map<String, UnitCommand> UNITCOMMAND_MAP = new SimpleReader<>("mimex-commands.txt", UnitCommand::new).createFromResource();
    static final Map<String, LAccess> LACCESS_MAP = new LAccessReader("mimex-laccess.txt").createFromResource();
    static final Map<String, LVar> LVAR_MAP = new LVarReader("mimex-vars.txt").createFromResource();

    static final Map<Integer, BlockType> BLOCK_ID_MAP = BLOCK_MAP.values().stream()
            .filter(block -> "shown".equals(block.visibility()))
            .collect(Collectors.toMap(BlockType::id, block -> block));
    static final Map<Integer, Item> ITEM_ID_MAP = ITEM_MAP.values().stream()
             .collect(Collectors.toMap(Item::id, obj -> obj, (a, b) -> a, HashMap::new));
    static final Map<Integer, Liquid> LIQUID_ID_MAP = LIQUID_MAP.values().stream()
            .collect(Collectors.toMap(Liquid::id, obj -> obj, (a, b) -> a, HashMap::new));
    static final Map<Integer, Unit> UNIT_ID_MAP = UNIT_MAP.values().stream()
            .collect(Collectors.toMap(Unit::id, obj -> obj, (a, b) -> a, HashMap::new));
    static final Map<Integer, UnitCommand> UNITCOMMAND_ID_MAP = UNITCOMMAND_MAP.values().stream()
            .collect(Collectors.toMap(UnitCommand::id, obj -> obj, (a, b) -> a, HashMap::new));

    private static final Map<String, MindustryContent> ALL_CONSTANTS =
            Stream.of(ITEM_MAP, LIQUID_MAP, UNIT_MAP, UNITCOMMAND_MAP, BLOCK_MAP)
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toMap(MindustryContent::name, t -> t));

    public static @Nullable MindustryContent get(String name) {
        return ALL_CONSTANTS.get(name);
    }

    public static int getId(String name) {
        MindustryContent content = ALL_CONSTANTS.get(name);
        return content == null ? -1 : content.id();
    }

    public static @Nullable Map<Integer, ? extends MindustryContent> getLookupMap(String type) {
        return switch (type) {
            case "block"    -> BLOCK_ID_MAP;
            case "liquid"   -> LIQUID_ID_MAP;
            case "item"     -> ITEM_ID_MAP;
            case "unit"     -> UNIT_ID_MAP;
            default         -> null;
        };
    }

    public static MindustryContent unregistered(String name) {
        return new MindustryContent() {
            @Override
            public ContentType contentType() {
                return ContentType.UNKNOWN;
            }

            @Override
            public String contentName() {
                return name.substring(1);
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public int id() {
                return -1;
            }
        };
    }

    // METADATA LOADING

    private static abstract class AbstractReader<T extends MindustryContent> {
        private final String resourceName;
        private final List<String> lines;
        private final List<String> header;

        protected abstract void parseHeader();
        protected abstract @Nullable T create(String[] columns);

        protected List<T> createUnregistered() {
            return List.of();
        }

        public AbstractReader(String resource) {
            this.resourceName = resource;
            try (InputStream input = Objects.requireNonNull(BlockTypeReader.class.getResourceAsStream(resource))) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                lines = reader.lines()
                        .filter(l -> !l.startsWith("//") && !l.isBlank())
                        .toList();
                header = List.of(lines.getFirst().split(";"));
                parseHeader();
            } catch (IOException e) {
                throw new RuntimeException("Cannot read resource " + resource, e);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing file " + resource, e);
            }
        }

        protected int findColumn(String columnName) {
            int index = header.indexOf(columnName);
            if (index < 0) {
                throw new IllegalStateException("Cannot locate column " + columnName + " in " + lines.getFirst());
            }
            return index;
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

    private static class SimpleReader<T extends MindustryContent> extends AbstractReader<T> {
        private interface ItemConstructor<T extends MindustryContent> {
            T construct(String contentName, String name, int id);
        }

        private final ItemConstructor<T> itemConstructor;
        private int name, id;

        public SimpleReader(String resource, ItemConstructor<T> itemConstructor) {
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
                    Integer.parseInt(columns[id]));
        }
    }

    private static class BlockTypeReader extends AbstractReader<BlockType> {
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

    private static class LAccessReader extends AbstractReader<LAccess> {
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

    private static class LVarReader extends AbstractReader<LVar> {
        private int name, global, isobj, constant, numval;

        public LVarReader(String resource) {
            super(resource);
        }

        @Override
        protected List<LVar> createUnregistered() {
            return List.of(
                    new LVar("configure", "@configure", false, false, false, 0.0),
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
}
