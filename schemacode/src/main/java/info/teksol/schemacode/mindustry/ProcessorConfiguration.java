package info.teksol.schemacode.mindustry;

import info.teksol.mindcode.compiler.CompilerFacade;
import info.teksol.mindcode.compiler.CompilerOutput;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstLink;
import info.teksol.schemacode.ast.AstProcessor;
import info.teksol.schemacode.config.ByteArray;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.schema.Block;
import info.teksol.schemacode.schema.BlockPosition;
import info.teksol.schemacode.schema.SchematicsBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public record ProcessorConfiguration(List<Link> links, String code) implements Configuration {

    public static final ProcessorConfiguration EMPTY = new ProcessorConfiguration(List.of(), "");

    private static final int maxByteLength = 1024 * 500;

    private static final Charset charset = StandardCharsets.UTF_8;

    @Override
    public Configuration encode(Block block) {
        int blockX = block.x();
        int blockY = block.y();
        byte[] bytes = code.getBytes(charset);
        try {
            var byteStream = new ByteArrayOutputStream();
            var stream = new DataOutputStream(new DeflaterOutputStream(byteStream));

            //current version of config format
            stream.write(1);

            //write string data
            stream.writeInt(bytes.length);
            stream.write(bytes);

            stream.writeInt(links.size());
            for (Link link : links) {
                stream.writeUTF(link.name());
                stream.writeShort(link.x() - blockX);
                stream.writeShort(link.y() - blockY);
            }

            stream.close();

            return new ByteArray(byteStream.toByteArray());
        } catch (IOException e) {
            throw new SchematicsInternalError(e, "Error decoding processor configuration.");
        }
    }

    public static ProcessorConfiguration decode(ByteArray array, Position position) {
        byte[] data = array.bytes();
        try (DataInputStream stream = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)))) {
            int version = stream.read();
            int xref = position.x();
            int yref = position.y();

            int length = stream.readInt();
            if (length > maxByteLength) throw new IOException("Malformed logic data! Length: " + length);
            byte[] bytes = new byte[length];
            stream.readFully(bytes);
            String code = new String(bytes, charset);

            // Links
            List<Link> links = new ArrayList<>();
            int total = stream.readInt();

            if (version == 0) {
                //old version just had links, ignore those
                for (int i = 0; i < total; i++) {
                    stream.readInt();
                }
            } else {
                for (int i = 0; i < total; i++) {
                    String name = stream.readUTF();
                    int x = xref + stream.readShort();
                    int y = yref + stream.readShort();
                    links.add(new Link(name, x, y));
                }
            }

            return new ProcessorConfiguration(links, code);
        } catch (IOException ex) {
            throw new RuntimeException("Error decoding processor configuration.", ex);
        }
    }

    @Override
    public ProcessorConfiguration remap(UnaryOperator<Position> mapping) {
        List<Link> links = this.links.stream().map(l -> new Link(l.name, mapping.apply(l.position))).toList();
        return new ProcessorConfiguration(links, code);
    }

    public static ProcessorConfiguration fromAstConfiguration(SchematicsBuilder builder, AstProcessor processor, Position position) {
        List<Link> links = processor.links().stream()
                .mapMulti((AstLink l, Consumer<Link> c) -> l.getProcessorLinks(c, builder, position))
                .distinct()
                .toList();

        // Detect link names used more than once
        Map<String, List<Link>> linksByName = links.stream().collect(Collectors.groupingBy(Link::name));
        linksByName.values().stream()
                .filter(v -> v.size() > 1)
                .forEachOrdered(l -> builder.error("Block link name '%s' used more than once.", l.get(0).name()));

        // Detect blocks linked more than once
        Map<Position, List<Link>> linksByPosition = links.stream().collect(Collectors.groupingBy(Link::position));
        linksByPosition.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEachOrdered(l -> builder.error("Multiple links for block at position %s: '%s'.",
                        l.getKey().toStringAbsolute(),
                        l.getValue().stream().map(Link::name).collect(Collectors.joining("', '"))));

        // Verify link names
        links.stream()
                .filter(l -> !compatibleLinkName(builder, l))
                .forEachOrdered(l -> builder.error("Incompatible link name '%s' for block type '%s'.", l.name,
                        builder.getBlockPosition(l.position).blockType().name()));

        String mlog = convertToMlog(builder, processor);
        return new ProcessorConfiguration(links, mlog);
    }

    private static boolean compatibleLinkName(SchematicsBuilder builder, Link link) {
        BlockPosition position = builder.getBlockPosition(link.position);
        if (position == null) return true;
        String baseName = position.blockType().getBaseLinkName();
        return link.name().startsWith(baseName) && link.name.substring(baseName.length()).matches("[1-9]\\d*");
    }

    private static String convertToMlog(SchematicsBuilder builder, AstProcessor processor) {
        return switch (processor.language()) {
            case NONE -> "";
            case MLOG -> processor.program().getProgramText(builder);
            case MINDCODE -> {
                builder.info("Compiling %s", processor.program().getProgramId(builder));
                CompilerOutput<String> output = CompilerFacade.compile(processor.program().getProgramText(builder),
                        builder.getCompilerProfile());
                output.messages().forEach(builder::addMessage);
                if (output.hasErrors()) {
                    builder.error("Compile errors in Mindcode source code.");
                    yield "";
                }
                yield output.output();
            }
        };
    }

    public record Link(String name, Position position) implements Comparable<Link> {
        public Link(String name, int x, int y) {
            this(name, new Position(x, y));
        }

        public int x() {
            return position.x();
        }

        public int y() {
            return position.y();
        }

        @Override
        public int compareTo(Link o) {
            int pos = position.compareTo(o.position);
            return pos == 0 ? name.compareTo(o.name) : pos;
        }
    }
}
