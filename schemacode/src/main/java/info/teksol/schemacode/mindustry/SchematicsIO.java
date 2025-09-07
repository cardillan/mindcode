package info.teksol.schemacode.mindustry;

import info.teksol.mc.common.SourcePosition;
import info.teksol.mc.mindcode.logic.mimex.*;
import info.teksol.mc.util.Tuple2;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.SchematicsMetadata;
import info.teksol.schemacode.config.*;
import info.teksol.schemacode.schematics.Block;
import info.teksol.schemacode.schematics.BlockPositionMap;
import info.teksol.schemacode.schematics.Schematic;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class SchematicsIO {
    private static final Map<String, String> FALLBACK = buildFallbackMap();
    private static final byte[] HEADER = {'m', 's', 'c', 'h'};
    private static final byte VERSION = 1;

    public static void writeMsch(Schematic schematic, OutputStream output) throws IOException {
        output.write(HEADER);
        output.write(VERSION);

        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(output))) {
            stream.writeShort(schematic.width());
            stream.writeShort(schematic.height());
            Map<String, String> map = createMasterMap(schematic);
            stream.writeByte(map.size());
            for (var e : map.entrySet()) {
                stream.writeUTF(e.getKey());
                stream.writeUTF(e.getValue());
            }
            List<BlockType> typeList = schematic.blocks().stream().map(Block::blockType).distinct().toList();
            stream.writeByte(typeList.size());
            for (BlockType t : typeList) {
                stream.writeUTF(t.contentName());
            }

            stream.writeInt(schematic.blocks().size());
            for (Block block : schematic.blocks()) {
                stream.writeByte(typeList.indexOf(block.blockType()));
                stream.writeInt(block.position().pack());
                writeObject(stream, block.configuration().as(block.configurationClass()).encode(block));
                stream.writeByte(block.direction().ordinal());
            }
        }
    }

    private static String strNull(String str) {
        return str == null ? "" : str;
    }

    private static Map<String, String> createMasterMap(Schematic schematic) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", strNull(schematic.name()));
        map.put("contentMap", createContentMap(schematic));
        map.put("description", strNull(schematic.description()));
        if (!schematic.labels().isEmpty()) {
            map.put("labels", encodeLabels(schematic.labels()));
        }
        return map;
    }

    private static String createContentMap(Schematic schematic) {
        Map<Integer, Map<String, Integer>> map = new HashMap<>();
        for (Block block : schematic.blocks()) {
            if (block.configuration() instanceof ContentConfiguration cfg) {
                Map<String, Integer> inner = map.computeIfAbsent(cfg.getContentType().id, k -> new HashMap<>());
                inner.put(cfg.getContentName(), cfg.getId());
            }
        }

        return contentMapToJson(map);
    }

    public static void write(Schematic build, OutputStream output) throws IOException {
        BlockPositionMap<Block> map = BlockPositionMap.builderToMindustry(m -> {}, build.blocks());
        List<Block> blocks = build.blocks().stream().map(b -> b.remap(map::translate)).toList();
        Schematic msch = new Schematic(build.name(), build.filename(), build.description(), build.labels(), build.width(), build.height(), blocks);
        writeMsch(msch, output);
    }


    static Tuple2<DataInputStream, Integer> skipHeader(InputStream input) throws IOException {
        for (byte b : HEADER) {
            if (input.read() != b) {
                throw new IOException("Not a schematic file (missing header).");
            }
        }

        int ver = input.read();
        return Tuple2.of(new DataInputStream(new InflaterInputStream(input)), ver);
    }

    public static Schematic readMsch(InputStream input) throws IOException {
        Tuple2<DataInputStream, Integer> inputs = skipHeader(input);
        int ver = inputs.e2();

        try (DataInputStream stream = inputs.e1()) {
            short width = stream.readShort();
            short height = stream.readShort();

            if (width > 128 || height > 128) {
                throw new IOException("Invalid schematic: Too large (max possible size is 128x128).");
            }

            int entries = stream.readUnsignedByte();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < entries; i++) {
                map.put(stream.readUTF(), stream.readUTF());
            }

            // Tags are called "labels" inside the schematic structure
            List<String> labels = decodeLabels(map.getOrDefault("labels", ""));

            List<BlockType> typeMap = new ArrayList<>();
            byte length = stream.readByte();
            for (int i = 0; i < length; i++) {
                String name = stream.readUTF();
                BlockType block = SchematicsMetadata.getMetadata().getBlockByName("@" + FALLBACK.getOrDefault(name, name));
                if (block == null) {
                    throw new IOException("Unknown block type '@" + name + "'.");
                }
                typeMap.add(block);
            }

            int total = stream.readInt();

            if (total > 128 * 128) {
                throw new IOException("Invalid schematic: Too many blocks.");
            }

            final ContentMapper mapper;
            if (map.containsKey("contentMap")) {
                Map<Integer, Map<String, Integer>> nameMap = contentMapFromJson(map.get("contentMap"));
                Map<Integer, Map<Integer, MindustryContent>> contentMap = new HashMap<>();
                nameMap.forEach((key, value) -> {
                    ContentType contentType = ContentType.byId(key);
                    if (contentType == null || !contentType.hasLookup) return;

                    Map<Integer, MindustryContent> inner = new HashMap<>();
                    contentMap.put(key, inner);
                    value.forEach((k, v) -> {
                        MindustryContent content = SchematicsMetadata.getMetadata().getNamedContent(contentType, k);
                        if (content == null) throw new SchematicsInternalError("Unknown content '%s' for type '%s'.", k, contentType.name());
                        inner.put(v, content);
                    });
                });
                mapper = (type, id) -> contentMap.getOrDefault(type.id, Map.of()).get(id);
            } else {
                mapper = SchematicsMetadata.getMetadata()::getNamedContentById;
            }

            List<Block> blocks = new ArrayList<>();
            for (int index = 0, i = 0; i < total; i++) {
                BlockType blockType = typeMap.get(stream.readByte());
                Position position = Position.unpack(stream.readInt());
                Configuration raw = ver == 0 ? mapConfig(blockType, stream.readInt(), position) : readObject(stream, mapper);
                Direction direction = Direction.convert(stream.readByte());
                if (!"@air".equals(blockType.name())) {
                    Configuration config = convert(blockType, position, raw);
                    blocks.add(new Block(SourcePosition.EMPTY, index++, List.of(), blockType, position, direction, config));
                }
            }

            String name = map.getOrDefault("name", "");
            String description = map.getOrDefault("description", "");
            return new Schematic(name, "", description, labels, width, height, blocks);
        }
    }

    public static Schematic read(String filename, InputStream input) throws IOException {
        Schematic msch = readMsch(input);
        BlockPositionMap<Block> map = BlockPositionMap.mindustryToBuilder(m -> {}, msch.blocks());
        List<Block> blocks = msch.blocks().stream().map(b -> b.remap(map::translate)).toList();
        return new Schematic(msch.name(), filename, msch.description(), msch.labels(), msch.width(), msch.height(), blocks);
    }

    @SuppressWarnings("UnnecessaryDefault")
    public static Configuration convert(BlockType blockType, Position position, Configuration raw) {
        if (raw == EmptyConfiguration.EMPTY) {
            // Cannot decode
            return raw;
        }

        ConfigurationType configurationType = ConfigurationType.fromBlockType(blockType);
        return switch (configurationType) {
            case NONE -> raw.as(EmptyConfiguration.class);
            case BOOLEAN -> raw.as(BooleanConfiguration.class);
            case COLOR -> Color.decode(raw.as(IntConfiguration.class).value());
            case CONNECTION -> raw.as(Position.class).add(position);
            case CONNECTIONS -> raw.as(PositionArray.class).remap(position::add);
            case INTEGER -> raw.as(IntConfiguration.class);
            case BLOCK -> raw.as(BlockConfiguration.class);
            case ITEM -> raw.as(ItemConfiguration.class);
            case LIQUID -> raw.as(LiquidConfiguration.class);
            case PROCESSOR -> ProcessorConfiguration.decode(raw.as(ByteArray.class), position);
            case TEXT -> raw.as(TextConfiguration.class);
            case UNIT -> raw.as(UnitConfiguration.class);
            case UNIT_COMMAND -> raw.as(UnitCommandConfiguration.class);
            case UNIT_OR_BLOCK -> raw.as(UnitOrBlockConfiguration.class);
            case UNIT_PLAN -> selectUnitPlan(raw.as(IntConfiguration.class), blockType);
            default -> throw new SchematicsInternalError("Unhandled configuration type %s.", configurationType);
        };
    }

    private static Configuration selectUnitPlan(IntConfiguration integer, BlockType blockType) {
        if (ConfigurationType.fromBlockType(blockType) != ConfigurationType.UNIT_PLAN) {
            throw new SchematicsInternalError("Block '%s' does not support UNIT_PLAN configuration.", blockType.name());
        }

        int index = integer.value();
        if (index < -1 || index >= blockType.unitPlans().size()) {
            throw new SchematicsInternalError("Invalid plan index %d for unit factory %s.", index, blockType.name());
        }

        return index < 0 ? EmptyConfiguration.EMPTY : new UnitPlan(blockType.unitPlans().get(index));
    }

    private static Configuration mapConfig(BlockType block, int value, Position position) {
        return switch (Implementation.fromBlockType(block)) {
            case SORTER, UNLOADER, ITEMSOURCE -> ItemConfiguration.forId(value);
            case LIQUIDSOURCE -> LiquidConfiguration.forId(value);
            case MASSDRIVER, ITEMBRIDGE -> Position.unpack(value).sub(position);
            case LIGHTBLOCK -> new IntConfiguration(value);
            default -> null;
        };
    }

    private static Configuration readObject(DataInputStream stream, ContentMapper mapper) throws IOException {
        byte b;
        byte type = stream.readByte();
        return switch (type) {
            case 0 -> EmptyConfiguration.EMPTY;
            case 1 -> new IntConfiguration(stream.readInt());
            case 2 -> new LongConfiguration(stream.readLong());
            case 3 -> new FloatConfiguration(stream.readFloat());
            case 4 -> readString(stream);
            case 5 -> {
                b = stream.readByte();
                ContentType contentType = ContentType.byId(b);
                if (contentType == null || !contentType.hasLookup) {
                    throw new UnsupportedOperationException("Unsupported item type ID " + b);
                }

                int id = stream.readShort();
                MindustryContent mindustryContent = mapper.get(contentType, id);
                yield Objects.requireNonNull(switch (contentType) {
                    case ITEM   -> ItemConfiguration.forItem((Item) mindustryContent);
                    case BLOCK  -> BlockConfiguration.forBlockType((BlockType) mindustryContent);
                    case LIQUID -> LiquidConfiguration.forLiquid((Liquid) mindustryContent);
                    case UNIT   -> UnitConfiguration.forUnit((Unit) mindustryContent);
                    default     -> throw new UnsupportedOperationException("Unsupported item type " + contentType);
                });
            }
            case 6 -> Array.create(Integer.class, stream.readShort(), stream::readInt);
            case 7 -> new Position(stream.readInt(), stream.readInt());
            case 8 ->
                    PositionArray.create(stream.readByte(), () -> info.teksol.schemacode.mindustry.Position.unpack(stream.readInt()));
            case 9 -> new UnhandledItem("TechNode", "byte=" + stream.readByte() + ", short=" + stream.readShort());
            case 10 -> BooleanConfiguration.of(stream.readBoolean());
            case 11 -> new DoubleConfiguration(stream.readDouble());
            case 12 -> new UnhandledItem("BuildingBox ", "int=" + stream.readInt());
            case 13 -> new UnhandledItem("LAccess ", "short=" + stream.readShort());
            case 14 -> {
                byte[] bts = new byte[stream.readInt()];
                stream.readFully(bts);
                yield new ByteArray(bts);
            }
            case 15 -> {
                stream.readByte();
                yield EmptyConfiguration.EMPTY;
            } //unit command
            case 16 -> Array.create(Boolean.class, stream.readInt(), stream::readBoolean);
            case 17 -> new UnhandledItem("Unit ", "int=" + stream.readInt());
            case 18 ->
                    Array.create(Vector.class, stream.readShort(), () -> new Vector(stream.readFloat(), stream.readFloat()));
            case 19 -> new Vector(stream.readFloat(), stream.readFloat());
            case 20 -> new UnhandledItem("Team ", "unsigned byte=" + stream.readUnsignedByte());
            case 23 -> UnitCommandConfiguration.forId(stream.readUnsignedShort());
            default -> throw new IllegalArgumentException("Unknown object type: " + type);
        };
    }

    public static void writeObject(DataOutputStream s, Configuration configuration) throws IOException {
        switch (configuration) {
            case EmptyConfiguration c -> s.writeByte(0);
            case IntConfiguration c -> {
                s.writeByte(1);
                s.writeInt(c.value());
            }
            case LongConfiguration c -> {
                s.writeByte(2);
                s.writeLong(c.value());
            }
            case FloatConfiguration c -> {
                s.writeByte(3);
                s.writeFloat(c.value());
            }
            case TextConfiguration c -> {
                s.writeByte(4);
                s.writeByte(c.value() == null ? 0 : 1);
                if (c.value() != null) {
                    s.writeUTF(c.value());
                }
            }
            case ItemConfiguration c -> {
                s.writeByte(5);
                s.writeByte(0);
                s.writeShort(c.getId());
            }
            case BlockConfiguration c -> {
                s.writeByte(5);
                s.writeByte(1);
                s.writeShort(c.getId());
            }
            case LiquidConfiguration c -> {
                s.writeByte(5);
                s.writeByte(4);
                s.writeShort(c.getId());
            }
            case UnitConfiguration c -> {
                s.writeByte(5);
                s.writeByte(6);
                s.writeShort(c.getId());
            }
            case Array<?> a -> {
                switch (a.dataClass().getSimpleName()) {
                    case "Integer" -> {
                        s.writeByte(6);
                        s.writeShort(a.size());
                        a.store(i -> s.writeInt((Integer) i));
                    }
                    case "Boolean" -> {
                        s.writeByte(16);
                        s.writeInt(a.size());
                        a.store(b -> s.writeBoolean((Boolean) b));
                    }
                    case "Vector" -> {
                        s.writeByte(18);
                        s.writeShort(a.size());
                        a.store(v -> {
                            Vector vector = (Vector) v;
                            s.writeFloat(vector.x());
                            s.writeFloat(vector.y());
                        });
                    }
                    default ->
                            throw new SchematicsInternalError("Cannot store unhandled array of %s", a.dataClass().getSimpleName());
                }
            }
            case Position c -> {
                s.writeByte(7);
                s.writeInt(c.x());
                s.writeInt(c.y());
            }
            case PositionArray a -> {
                s.writeByte(8);
                s.writeByte(a.size());
                a.store(p -> s.writeInt(p.pack()));
            }
            case BooleanConfiguration c -> {
                s.writeByte(10);
                s.writeBoolean(c.value());
            }
            case DoubleConfiguration c -> {
                s.writeByte(11);
                s.writeDouble(c.value());
            }
            case ByteArray a -> {
                s.writeByte(14);
                s.writeInt(a.size());
                s.write(a.bytes());
            }
            case Vector c -> {
                s.writeByte(19);
                s.writeFloat(c.x());
                s.writeFloat(c.y());
            }
            case UnitCommandConfiguration c -> {
                s.writeByte(23);
                s.writeShort(c.getId());
            }
            default -> throw new SchematicsInternalError("Cannot store unhandled item %s", configuration);
        }
    }

    private static Configuration readString(DataInputStream stream) throws IOException {
        byte exists = stream.readByte();
        if (exists != 0) {
            return new TextConfiguration(stream.readUTF());
        } else {
            return new TextConfiguration(null);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static Map<String, String> buildFallbackMap() {
        Map<String, String> map = new HashMap<>();
        map.put("dart-mech-pad", "legacy-mech-pad");
        map.put("dart-ship-pad", "legacy-mech-pad");
        map.put("javelin-ship-pad", "legacy-mech-pad");
        map.put("trident-ship-pad", "legacy-mech-pad");
        map.put("glaive-ship-pad", "legacy-mech-pad");
        map.put("alpha-mech-pad", "legacy-mech-pad");
        map.put("tau-mech-pad", "legacy-mech-pad");
        map.put("omega-mech-pad", "legacy-mech-pad");
        map.put("delta-mech-pad", "legacy-mech-pad");

        map.put("draug-factory", "legacy-unit-factory");
        map.put("spirit-factory", "legacy-unit-factory");
        map.put("phantom-factory", "legacy-unit-factory");
        map.put("wraith-factory", "legacy-unit-factory");
        map.put("ghoul-factory", "legacy-unit-factory-air");
        map.put("revenant-factory", "legacy-unit-factory-air");
        map.put("dagger-factory", "legacy-unit-factory");
        map.put("crawler-factory", "legacy-unit-factory");
        map.put("titan-factory", "legacy-unit-factory-ground");
        map.put("fortress-factory", "legacy-unit-factory-ground");

        map.put("mass-conveyor", "payload-conveyor");
        map.put("vestige", "scepter");
        map.put("turbine-generator", "steam-generator");

        map.put("rocks", "stone-wall");
        map.put("sporerocks", "spore-wall");
        map.put("icerocks", "ice-wall");
        map.put("dunerocks", "dune-wall");
        map.put("sandrocks", "sand-wall");
        map.put("shalerocks", "shale-wall");
        map.put("snowrocks", "snow-wall");
        map.put("saltrocks", "salt-wall");
        map.put("dirtwall", "dirt-wall");

        map.put("ignarock", "basalt");
        map.put("holostone", "dacite");
        map.put("holostone-wall", "dacite-wall");
        map.put("rock", "boulder");
        map.put("snowrock", "snow-boulder");
        map.put("cliffs", "stone-wall");
        map.put("craters", "crater-stone");
        map.put("deepwater", "deep-water");
        map.put("water", "shallow-water");
        map.put("sand", "sand-floor");
        map.put("slag", "molten-slag");

        map.put("cryofluidmixer", "cryofluid-mixer");
        map.put("block-forge", "constructor");
        map.put("block-unloader", "payload-unloader");
        map.put("block-loader", "payload-loader");
        map.put("thermal-pump", "impulse-pump");
        map.put("alloy-smelter", "surge-smelter");
        map.put("steam-vent", "rhyolite-vent");
        map.put("fabricator", "tank-fabricator");
        map.put("basic-reconstructor", "refabricator");

        return Map.copyOf(map);
    }

    static List<String> decodeLabels(String strLabels) {
        StringBuilder accumulator = new StringBuilder();
        List<String> list = new ArrayList<>();
        boolean escape = false;
        boolean quotes = false;
        if (strLabels.startsWith("[") && strLabels.endsWith("]")) {
            int index = 1;
            while (index < strLabels.length() - 1) {
                char ch = strLabels.charAt(index);

                if (escape) {
                    accumulator.append(ch);
                    escape = false;
                    index++;
                    continue;
                }

                switch (ch) {
                    case '\\' -> escape = true;
                    case '"' -> quotes = !quotes;
                    case ',' -> {
                        if (quotes) {
                            accumulator.append(ch);
                        } else {
                            list.add(accumulator.toString());
                            accumulator.setLength(0);
                        }
                    }
                    default -> accumulator.append(ch);
                }
                index++;
            }
            list.add(accumulator.toString());
            return List.copyOf(list);
        } else {
            return List.of();
        }
    }

    static String encodeLabels(List<String> labels) {
        return labels.stream().map(SchematicsIO::encodeLabel).collect(Collectors.joining(",", "[", "]"));
    }

    static String encodeLabel(String label) {
        if (label.matches(".*[,\\[\\]\"\\\\].*")) {
            StringBuilder sbr = new StringBuilder("\"");
            for (int i = 0; i < label.length(); i++) {
                char ch = label.charAt(i);
                if (ch == '"') sbr.append('\\');
                sbr.append(ch);
            }
            return sbr.append('"').toString();
        } else {
            return label;
        }
    }

    /**
     * Converter of an ID to a content instance.
     */
    private interface ContentMapper {
        MindustryContent get(ContentType type, int id);
    }

    /**
     * Deserialize JSON like { "0": {...}, "4": {...} }
     * into a Map<Integer, Map<String, Integer>>.
     */
    public static Map<Integer, Map<String, Integer>> contentMapFromJson(String json) {
        Map<Integer, Map<String, Integer>> result = new HashMap<>();
        json = json.replaceAll("(?m)(?<=[{,])\\s*(?!\")([A-Za-z_][A-Za-z0-9_-]*)\\s*:", "\"$1\":");
        json = json.replaceAll("(?m)(?<=[{,])\\s*(?!\")(-?\\d+)\\s*:", "\"$1\":");

        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject root = reader.readObject();

            for (String key : root.keySet()) {
                int outerKey = Integer.parseInt(key);
                JsonObject innerObj = root.getJsonObject(key);

                Map<String, Integer> innerMap = new HashMap<>();
                for (String innerKey : innerObj.keySet()) {
                    innerMap.put(innerKey, innerObj.getInt(innerKey));
                }

                result.put(outerKey, innerMap);
            }
        }
        return result;
    }

    /**
     * Serialize Map<Integer, Map<String, Integer>> into a JsonObject.
     */
    public static String contentMapToJson(Map<Integer, Map<String, Integer>> map) {
        JsonObjectBuilder rootBuilder = Json.createObjectBuilder();

        for (Map.Entry<Integer, Map<String, Integer>> outer : map.entrySet()) {
            JsonObjectBuilder innerBuilder = Json.createObjectBuilder();
            for (Map.Entry<String, Integer> inner : outer.getValue().entrySet()) {
                innerBuilder.add(inner.getKey(), inner.getValue());
            }
            rootBuilder.add(String.valueOf(outer.getKey()), innerBuilder.build());
        }

        return rootBuilder.build().toString().replaceAll("\"", "");
    }

    public static void main(String[] argv) {

    }
}
