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

import static info.teksol.mindcode.mimex.BlockType.NAME_MAP;

public class NumberedConstants {
    static final Map<String, Item> ITEM_MAP = new ItemReader().createFromResource();
    static final Map<String, Liquid> LIQUID_MAP = new LiquidReader().createFromResource();
    static final Map<String, Unit> UNIT_MAP = new UnitReader().createFromResource();
    static final Map<String, UnitCommand> UNITCOMMAND_MAP = new UnitCommandReader().createFromResource();

    static final Map<Integer, Item> ITEM_ID_MAP = ITEM_MAP.values().stream()
            .collect(Collectors.toMap(Item::id, item -> item));
    static final Map<Integer, Liquid> LIQUID_ID_MAP = LIQUID_MAP.values().stream()
            .collect(Collectors.toMap(Liquid::id, liquid -> liquid));
    static final Map<Integer, Unit> UNIT_ID_MAP = UNIT_MAP.values().stream()
            .collect(Collectors.toMap(Unit::id, unit -> unit));
    static final Map<Integer, UnitCommand> UNITCOMMAND_ID_MAP = UNITCOMMAND_MAP.values().stream()
            .collect(Collectors.toMap(UnitCommand::id, command -> command));

    private static final Map<String, NumberedConstant> ALL_CONSTANTS =
            Stream.of(ITEM_MAP, LIQUID_MAP, UNIT_MAP, NAME_MAP)
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toMap(NumberedConstant::baseName, t -> t));

    public static int getId(String baseName) {
        NumberedConstant constant = ALL_CONSTANTS.get(baseName);
        return constant == null ? -1 : constant.id();
    }

    private List<String> parseUnitPlans(String unitPlans) {
        return unitPlans.isBlank() ? List.of() : Stream.of(unitPlans.split("\\|")).map("@"::concat).toList();
    }

    private static abstract class AbstractReader<T extends NumberedConstant> {
        private final String resourceName;
        private final List<String> lines;
        private final List<String> header;
        protected final int name, id;

        protected abstract T create(String[] columns);

        public AbstractReader(String resourceName) {
            this.resourceName = resourceName;
            try (InputStream input = BlockTypeReader.class.getResourceAsStream(resourceName)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                lines = reader.lines()
                        .filter(l -> !l.startsWith("//") && !l.isBlank())
                        .toList();
                header = List.of(lines.get(0).split(";"));
                name = findColumn("name");
                id = findColumn("id");
            } catch (IOException e) {
                throw new RuntimeException("Cannot read resource " + resourceName, e);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing file " + resourceName, e);
            }
        }

        private int findColumn(String columnName) {
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

    private static class ItemReader extends AbstractReader<Item> {
        public ItemReader() {
            super("mimex-items.txt");
        }

        @Override
        protected Item create(String[] columns) {
            return new Item(
                    columns[name],
                    "@" + columns[name],
                    Integer.parseInt(columns[id]));
        }
    }

    private static class LiquidReader extends AbstractReader<Liquid> {
        public LiquidReader() {
            super("mimex-liquids.txt");
        }

        @Override
        protected Liquid create(String[] columns) {
            return new Liquid(
                    columns[name],
                    "@" + columns[name],
                    Integer.parseInt(columns[id]));
        }
    }

    private static class UnitReader extends AbstractReader<Unit> {
        public UnitReader() {
            super("mimex-units.txt");
        }

        @Override
        protected Unit create(String[] columns) {
            return new Unit(
                    columns[name],
                    "@" + columns[name],
                    Integer.parseInt(columns[id]));
        }
    }

    private static class UnitCommandReader extends AbstractReader<UnitCommand> {
        public UnitCommandReader() {
            super("mimex-commands.txt");
        }

        @Override
        protected UnitCommand create(String[] columns) {
            return new UnitCommand(
                    columns[name],
                    "@" + columns[name],
                    Integer.parseInt(columns[id]));
        }
    }
}
