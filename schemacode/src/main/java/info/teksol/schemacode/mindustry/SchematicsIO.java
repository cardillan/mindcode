package info.teksol.schemacode.mindustry;

import info.teksol.mindcode.Tuple2;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.config.*;
import info.teksol.schemacode.mimex.BlockType;
import info.teksol.schemacode.schema.Block;
import info.teksol.schemacode.schema.BlockPositionMap;
import info.teksol.schemacode.schema.Schematics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class SchematicsIO {
    private static final Map<String, String> FALLBACK = buildFallbackMap();
    private static final byte[] HEADER = {'m', 's', 'c', 'h'};
    private static final byte VERSION = 1;

    public static void writeMsch(Schematics schematics, OutputStream output) throws IOException {
        output.write(HEADER);
        output.write(VERSION);

        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(output))) {
            stream.writeShort(schematics.width());
            stream.writeShort(schematics.height());
            Map<String, String> tags = createTags(schematics);
            stream.writeByte(tags.size());
            for (var e : tags.entrySet()) {
                stream.writeUTF(e.getKey());
                stream.writeUTF(e.getValue());
            }
            List<BlockType> typeList = schematics.blocks().stream().map(Block::blockType).distinct().toList();
            stream.writeByte(typeList.size());
            for (BlockType t : typeList) {
                stream.writeUTF(t.name().substring(1));
            }

            stream.writeInt(schematics.blocks().size());
            for (Block block : schematics.blocks()) {
                stream.writeByte(typeList.indexOf(block.blockType()));
                stream.writeInt(block.position().pack());
                writeObject(stream, block.configuration().encode(block));
                stream.writeByte(block.direction().ordinal());
            }
        }
    }

    private static String strNull(String str) {
        return str == null ? "" : str;
    }

    private static Map<String, String> createTags(Schematics schematics) {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("name", strNull(schematics.name()));
        tags.put("description", strNull(schematics.description()));
        if (!schematics.labels().isEmpty()) {
            tags.put("labels", encodeLabels(schematics.labels()));
        }
        return tags;
    }

    public static void write(Schematics build, OutputStream output) throws IOException {
        BlockPositionMap map = BlockPositionMap.builderToMindustry(build);
        List<Block> blocks = build.blocks().stream().map(b -> b.remap(map::translate)).toList();
        Schematics msch = new Schematics(build.name(), build.description(), build.labels(), build.width(), build.height(), blocks);
        writeMsch(msch, output);
    }


    static Tuple2<DataInputStream, Integer> skipHeader(InputStream input) throws IOException {
        for (byte b : HEADER) {
            if (input.read() != b) {
                throw new IOException("Not a schematic file (missing header).");
            }
        }

        int ver = input.read();
        return new Tuple2<>(new DataInputStream(new InflaterInputStream(input)), ver);
    }

    public static Schematics readMsch(InputStream input) throws IOException {
        Tuple2<DataInputStream, Integer> inputs = skipHeader(input);
        int ver = inputs.getT2();

        try (DataInputStream stream = inputs.getT1()) {
            short width = stream.readShort();
            short height = stream.readShort();

            if (width > 128 || height > 128) {
                throw new IOException("Invalid schematic: Too large (max possible size is 128x128)");
            }

            int tags = stream.readUnsignedByte();
            Map<String, String> tagMap = new HashMap<>();
            for (int i = 0; i < tags; i++) {
                tagMap.put(stream.readUTF(), stream.readUTF());
            }

            // Tags are called "labels" inside schematics structure
            List<String> labels = decodeLabels(tagMap.getOrDefault("labels", ""));

            List<BlockType> typeMap = new ArrayList<>();
            byte length = stream.readByte();
            for (int i = 0; i < length; i++) {
                String name = stream.readUTF();
                BlockType block = BlockType.forName("@" + FALLBACK.getOrDefault(name, name));
                if (block == null) {
                    throw new IOException("Unknown block name " + name);
                }
                typeMap.add(block);
            }

            int total = stream.readInt();

            if (total > 128 * 128) {
                throw new IOException("Invalid schematic: Too many blocks.");
            }

            List<Block> blocks = new ArrayList<>();
            for (int i = 0; i < total; i++) {
                BlockType blockType = typeMap.get(stream.readByte());
                Position position = Position.unpack(stream.readInt());
                Configuration raw = ver == 0 ? mapConfig(blockType, stream.readInt(), position) : readObject(stream);
                Direction direction = Direction.convert(stream.readByte());
                if (!"@air".equals(blockType.name())) {
                    Configuration config = convert(blockType, position, raw);
                    blocks.add(new Block(List.of(), blockType, position, direction, config));
                }
            }

            String name = tagMap.getOrDefault("name", "");
            String description = tagMap.getOrDefault("description", "");
            return new Schematics(name, description, labels, width, height, blocks);
        }
    }

    public static Schematics read(InputStream input) throws IOException {
        Schematics msch = readMsch(input);
        BlockPositionMap map = BlockPositionMap.mindustryToBuilder(msch);
        List<Block> blocks = msch.blocks().stream().map(b -> b.remap(map::translate)).toList();
        return new Schematics(msch.name(), msch.description(), msch.labels(), msch.width(), msch.height(), blocks);
    }

    @SuppressWarnings("UnnecessaryDefault")
    public static Configuration convert(BlockType blockType, Position position, Configuration raw) {
        if (raw == EmptyConfiguration.EMPTY) {
            // Cannot decode
            return raw;
        }

        return switch (blockType.configurationType()) {
            case NONE           -> raw.as(EmptyConfiguration.class);
            case BOOLEAN        -> raw.as(BooleanConfiguration.class);
            case CONNECTION     -> raw.as(Position.class).add(position);
            case CONNECTIONS    -> raw.as(PositionArray.class).remap(position::add);
            case INTEGER        -> raw.as(IntConfiguration.class);
            case ITEM           -> raw.as(Item.class);
            case LIQUID         -> raw.as(Liquid.class);
            case PROCESSOR      -> ProcessorConfiguration.decode(raw.as(ByteArray.class), position);
            case TEXT           -> raw.as(TextConfiguration.class);
            default -> throw new SchematicsInternalError("Unhandled configuration type %s.", blockType.configurationType());
        };
    }

    private static Configuration mapConfig(BlockType block, int value, Position position) {
        return switch (block.implementation()) {
            case SORTER, UNLOADER, ITEMSOURCE   -> Item.forIndex(value);
            case LIQUIDSOURCE                   -> Liquid.forIndex(value);
            case MASSDRIVER, ITEMBRIDGE         -> Position.unpack(value).sub(position);
            case LIGHTBLOCK                     -> new IntConfiguration(value);
            default                             -> null;
        };
    }

    private static Configuration readObject(DataInputStream stream) throws IOException {
        byte b;
        byte type = stream.readByte();
        return switch (type) {
            case 0 -> EmptyConfiguration.EMPTY;
            case 1 -> new IntConfiguration(stream.readInt());
            case 2 -> new LongConfiguration(stream.readLong());
            case 3 -> new FloatConfiguration(stream.readFloat());
            case 4 -> readString(stream);
            case 5 -> switch (b = stream.readByte()) {
                case 0 -> Item.forIndex(stream.readShort());
                case 4 -> Liquid.forIndex(stream.readShort());
                default -> throw new UnsupportedOperationException("Unsupported item type " + b);
            };
            case 6 -> Array.create(Integer.class, stream.readShort(), stream::readInt);
            case 7 -> new Position(stream.readInt(), stream.readInt());
            case 8 -> PositionArray.create(stream.readByte(), () -> Position.unpack(stream.readInt()));
            case 9 -> new UnhandledItem("TechNode", "byte=" + stream.readByte() + ", short=" + stream.readShort());
            case 10 -> BooleanConfiguration.of(stream.readBoolean());
            case 11 -> new DoubleConfiguration(stream.readDouble());
            case 12 -> new UnhandledItem("BuildingBox ", "int=" + stream.readInt());
            case 13 -> new UnhandledItem("LAccess ", "short=" + stream.readShort());
            case 14 -> { byte[] bts = new byte[stream.readInt()]; stream.readFully(bts); yield new ByteArray(bts); }
            case 15 -> { stream.readByte(); yield EmptyConfiguration.EMPTY; } //unit command
            case 16 -> Array.create(Boolean.class, stream.readInt(), stream::readBoolean);
            case 17 -> new UnhandledItem("Unit ", "int=" + stream.readInt());
            case 18 -> Array.create(Vector.class, stream.readShort(), () -> new Vector(stream.readFloat(), stream.readFloat()));
            case 19 -> new Vector(stream.readFloat(), stream.readFloat());
            case 20 -> new UnhandledItem("Team ", "unsigned byte=" + stream.readUnsignedByte());
            default -> throw new IllegalArgumentException("Unknown object type: " + type);
        };
    }

    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    public static void writeObject(DataOutputStream s, Configuration configuration) throws IOException {
        switch (configuration) {
            case EmptyConfiguration c       -> { s.writeByte(0); }
            case IntConfiguration c         -> { s.writeByte(1); s.writeInt(c.value()); }
            case LongConfiguration c        -> { s.writeByte(2); s.writeLong(c.value()); }
            case FloatConfiguration c       -> { s.writeByte(3); s.writeFloat(c.value()); }
            case TextConfiguration c        -> { s.writeByte(4); s.writeByte(c.value() == null ? 0 : 1);
                if (c.value() != null) {
                    s.writeUTF(c.value());
                }
            }
            case Item c                     -> { s.writeByte(5); s.writeByte(0); s.writeShort(c.getId()); }
            case Liquid c                   -> { s.writeByte(5); s.writeByte(4); s.writeShort(c.getId()); }
            case Array<?> a                 -> { switch (a.dataClass().getSimpleName()) {
                case "Integer"  -> { s.writeByte(6); s.writeShort(a.size()); a.store(i -> s.writeInt((Integer) i)); }
                case "Boolean"  -> { s.writeByte(16); s.writeInt(a.size()); a.store(b -> s.writeBoolean((Boolean) b)); }
                case "Vector"   -> { s.writeByte(18); s.writeShort(a.size());
                    a.store(v -> { Vector vector = (Vector) v; s.writeFloat(vector.x()); s.writeFloat(vector.y()); } );
                }
                default -> throw new SchematicsInternalError("Cannot store unhandled array of %s", a.dataClass().getSimpleName());
            }}
            case Position c                 -> { s.writeByte(7); s.writeInt(c.x()); s.writeInt(c.y()); }
            case PositionArray a            -> { s.writeByte(8); s.writeByte(a.size()); a.store(p -> s.writeInt(p.pack())); }
            case BooleanConfiguration c     -> { s.writeByte(10); s.writeBoolean(c.value()); }
            case DoubleConfiguration c      -> { s.writeByte(11); s.writeDouble(c.value()); }
            case ByteArray a                -> { s.writeByte(14); s.writeInt(a.size()); s.write(a.bytes());}
            case Vector c                   -> { s.writeByte(19); s.writeFloat(c.x()); s.writeFloat(c.y()); }

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
                    case '\\'   -> escape = true;
                    case '"'    -> quotes = !quotes;
                    case ','    -> {
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
        return labels.stream().map(SchematicsIO::encodeLabel).collect(Collectors.joining(",","[","]"));
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
}
