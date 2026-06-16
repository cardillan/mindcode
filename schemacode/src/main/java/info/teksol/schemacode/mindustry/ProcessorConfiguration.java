package info.teksol.schemacode.mindustry;

import info.teksol.mc.common.SourceElement;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.config.ByteArray;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.schematics.Block;
import info.teksol.schemacode.schematics.SchematicsBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
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
            throw new SchematicsInternalError(e, "Error encoding processor configuration.");
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
        List<Link> links = this.links.stream().map(l -> new Link(l.name(), mapping.apply(l.position()))).toList();
        return new ProcessorConfiguration(links, code);
    }

    @Override
    public void validate(SchematicsBuilder builder, SourceElement astBlock, Block block) {
        validate(builder, astBlock, block, false);

        // Here we simply parse and reformat the code, even though it would be possible and more efficient
        // to avoid that for code compiled from Mindcode. This makes for a bit simpler design here.
        validate(builder, astBlock, block.withReformattedCode(builder.getCompilerProfile().getSchematicTarget()), true);
    }

    private void validate(SchematicsBuilder builder, SourceElement astBlock, Block block, boolean reformatted) {
        int maxSize = builder.getCompilerProfile().getProcessorSizeLimit();
        Configuration configuration = block.configuration().encode(block);
        if (configuration instanceof ByteArray array) {
            if (array.size() > maxSize) {
                builder.message(builder.getCompilerProfile().isEnforceSizeLimits() ? MessageLevel.ERROR : MessageLevel.WARNING,
                        astBlock, "%s configuration size of %,d bytes exceeds the maximum size of %,d bytes.",
                        reformatted ? "Reformatted processor" : "Processor", array.size(), maxSize);
            }
        } else {
            throw new SchematicsInternalError("Unexpected configuration type - expected ByteArray, got %s.", configuration.getClass().getSimpleName());
        }
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

        public Link withName(String name) {
            return Objects.equals(this.name, name) ? this : new Link(name, position);
        }
    }
}
