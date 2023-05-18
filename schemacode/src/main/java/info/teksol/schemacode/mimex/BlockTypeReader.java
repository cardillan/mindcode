package info.teksol.schemacode.mimex;

import info.teksol.schemacode.mindustry.ConfigurationType;
import info.teksol.schemacode.mindustry.Implementation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BlockTypeReader {

    static Map<String, BlockType> createFromResource() {
        return new BlockTypeReader().createFromResource0();
    }

    private static final String RESOURCE_NAME = "mimex-blocks.txt";
    private final List<String> lines;
    private final List<String> header;
    private final int name, implementation, size, hasPower, configurable, category, range, maxNodes, rotate, unitPlans;

    private BlockTypeReader() {
        try (InputStream input = BlockTypeReader.class.getResourceAsStream(RESOURCE_NAME)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            lines = reader.lines()
                    .filter(l -> !l.startsWith("//") && !l.isBlank())
                    .toList();
            header = List.of(lines.get(0).split(";"));
            name = findColumn("name");
            implementation = findColumn("subclass");
            size = findColumn("size");
            hasPower = findColumn("hasPower");
            configurable = findColumn("configurable");
            category = findColumn("category");
            maxNodes = findColumn("maxNodes");
            range = findColumn("range");
            rotate = findColumn("rotate");
            unitPlans = findColumn("unitPlans");
        } catch (IOException e) {
            throw new RuntimeException("Cannot read resource " + RESOURCE_NAME, e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + RESOURCE_NAME, e);
        }
    }

    private int findColumn(String columnName) {
        int index = header.indexOf(columnName);
        if (index < 0) {
            throw new IllegalStateException("Cannot locate column " + columnName + " in " + lines.get(0));
        }
        return index;
    }

    private Map<String, BlockType> createFromResource0() {
        try {
            ArrayList<BlockType> list = new ArrayList<>(lines.size() - 1);
            for (int i = 1; i < lines.size(); i++) {
                String[] columns = lines.get(i).split(";", -1);
                list.add(new BlockType(
                        "@" + columns[name],
                        Implementation.valueOf(columns[implementation].toUpperCase()),
                        Integer.parseInt(columns[size]),
                        Boolean.parseBoolean(columns[hasPower]),
                        Boolean.parseBoolean(columns[configurable]),
                        (columns[category]),
                        Float.parseFloat(columns[range]),
                        Integer.parseInt(columns[maxNodes]),
                        Boolean.parseBoolean(columns[rotate]),
                        parseUnitPlans(columns[unitPlans])));
            }
            return list.stream().collect(Collectors.toMap(BlockType::name, t -> t));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing file " + RESOURCE_NAME, e);
        }
    }

    private List<String> parseUnitPlans(String unitPlans) {
        return unitPlans.isBlank() ? List.of() : Stream.of(unitPlans.split("\\|")).map("@"::concat).toList();
    }

    public static void main(String[] argv) {
        createFromResource().values().stream()
                .filter(b -> b.implementation().configurationType() != ConfigurationType.NONE)
                .sorted(Comparator.comparing(BlockType::configurationType))
                .forEach(b -> System.out.printf("%-30s %s%n", b.name(), b.configurationType()));
    }
}
