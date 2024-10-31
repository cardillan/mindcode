package info.teksol.mindcode.mimex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MindustryContents {
    static final Map<String, BlockType> BLOCK_MAP = new BlockTypeReader("mimex-blocks.txt").createFromResource();
    static final Map<String, Item> ITEM_MAP = new SimpleReader<>("mimex-items.txt", Item::new).createFromResource();
    static final Map<String, Liquid> LIQUID_MAP = new SimpleReader<>("mimex-liquids.txt", Liquid::new).createFromResource();
    static final Map<String, Unit> UNIT_MAP = new SimpleReader<>("mimex-units.txt", Unit::new).createFromResource();
    static final Map<String, UnitCommand> UNITCOMMAND_MAP = new SimpleReader<>("mimex-commands.txt", UnitCommand::new).createFromResource();
    static final Map<String, LAccess> LACCESS_MAP = new LAccessReader("mimex-laccess.txt").createFromResource();

    static final Map<Integer, BlockType> BLOCK_ID_MAP = BLOCK_MAP.values().stream()
            .collect(Collectors.toMap(BlockType::id, block -> block));
    static final Map<Integer, Item> ITEM_ID_MAP = ITEM_MAP.values().stream()
            .collect(Collectors.toMap(Item::id, item -> item));
    static final Map<Integer, Liquid> LIQUID_ID_MAP = LIQUID_MAP.values().stream()
            .collect(Collectors.toMap(Liquid::id, liquid -> liquid));
    static final Map<Integer, Unit> UNIT_ID_MAP = UNIT_MAP.values().stream()
            .collect(Collectors.toMap(Unit::id, unit -> unit));
    static final Map<Integer, UnitCommand> UNITCOMMAND_ID_MAP = UNITCOMMAND_MAP.values().stream()
            .collect(Collectors.toMap(UnitCommand::id, command -> command));

    private static final Map<String, MindustryContent> ALL_CONSTANTS =
            Stream.of(ITEM_MAP, LIQUID_MAP, UNIT_MAP, UNITCOMMAND_MAP, BLOCK_MAP)
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toMap(MindustryContent::name, t -> t));

    public static MindustryContent get(String baseName) {
        return ALL_CONSTANTS.get(baseName);
    }

    public static int getId(String baseName) {
        MindustryContent content = ALL_CONSTANTS.get(baseName);
        return content == null ? -1 : content.id();
    }

    public static Map<Integer, ? extends MindustryContent> getLookupMap(String type) {
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
        protected abstract T create(String[] columns);

        public AbstractReader(String resource) {
            this.resourceName = resource;
            try (InputStream input = BlockTypeReader.class.getResourceAsStream(resource)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                lines = reader.lines()
                        .filter(l -> !l.startsWith("//") && !l.isBlank())
                        .toList();
                header = List.of(lines.get(0).split(";"));
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
                throw new IllegalStateException("Cannot locate column " + columnName + " in " + lines.get(0));
            }
            return index;
        }

        public Map<String, T> createFromResource() {
            try {
                ArrayList<T> list = new ArrayList<>(lines.size() - 1);
                for (int i = 1; i < lines.size(); i++) {
                    String[] columns = lines.get(i).split(";", -1);
                    list.add(create(columns));
                }
                return list.stream().collect(Collectors.toMap(T::name, t -> t));
            } catch (Exception e) {
                throw new RuntimeException("Error parsing file " + resourceName, e);
            }
        }
    }

    private static class SimpleReader<T extends MindustryContent> extends AbstractReader<T> {
        private interface ItemConstructor<T extends MindustryContent> {
            T construct(String name, int id);
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
            return new LAccess("@" + columns[name],
                    Boolean.parseBoolean(columns[senseable]),
                    Boolean.parseBoolean(columns[controls]),
                    Boolean.parseBoolean(columns[settable]),
                    columns[parameters]);
        }
    }
}
