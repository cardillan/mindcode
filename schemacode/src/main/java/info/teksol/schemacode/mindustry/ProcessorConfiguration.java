package info.teksol.schemacode.mindustry;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.common.SourceElement;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import info.teksol.schemacode.SchematicsInternalError;
import info.teksol.schemacode.ast.AstLink;
import info.teksol.schemacode.ast.AstProcessor;
import info.teksol.schemacode.config.ByteArray;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.schematics.Block;
import info.teksol.schemacode.schematics.BlockPosition;
import info.teksol.schemacode.schematics.SchematicsBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        List<Link> links = this.links.stream().map(l -> new Link(l.name, mapping.apply(l.position))).toList();
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


    public static ProcessorConfiguration fromAstConfiguration(SchematicsBuilder builder, AstProcessor processor, BlockPosition blockPos) {
        List<Link> links = processor.links().stream()
                .mapMulti((AstLink l, Consumer<Link> c) -> l.getProcessorLinks(c, builder, blockPos.position()))
                .distinct()
                .toList();

        // Detect link names used more than once
        Map<String, List<Link>> linksByName = links.stream().collect(Collectors.groupingBy(Link::name));
        linksByName.values().stream()
                .filter(v -> v.size() > 1)
                .forEachOrdered(l -> builder.error(processor, "Block link name '%s' used more than once.", l.getFirst().name()));

        // Detect blocks linked more than once
        Map<Position, List<Link>> linksByPosition = links.stream().collect(Collectors.groupingBy(Link::position));
        linksByPosition.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEachOrdered(l -> builder.error(processor, "Multiple links for block at position %s: '%s'.",
                        l.getKey().toStringAbsolute(),
                        l.getValue().stream().map(Link::name).collect(Collectors.joining("', '"))));

        // Gather expected link names
        Map<String, String> schematicLinks = links.stream()
                .collect(Collectors.toMap(Link::name, link -> getLinkBlockType(builder, link)));

        Map<String, String> symbolicNameMap = new HashMap<>();
        String mlog = convertToMlog(builder, processor, ProcessorType.fromBlockType(blockPos.blockType()), schematicLinks, symbolicNameMap);

        // Translate names
        links = links.stream().map(link -> link.withName(symbolicNameMap.getOrDefault(link.name(), link.name()))).toList();

        // A set of names which were resolved by Mindcode and therefore have been type-checked already
        Set<String> resolvedNames = new HashSet<>(symbolicNameMap.values());

        // Verify link names
        links.stream()
                .filter(l -> !resolvedNames.contains(l.name) && !compatibleLinkName(builder, l))
                .forEachOrdered(l -> builder.error(processor, "Incompatible link name '%s' for block type '%s'.", l.name,
                        builder.getBlockPosition(l.position).blockType().name()));

        return new ProcessorConfiguration(links, mlog);
    }

    private static String getLinkBlockType(SchematicsBuilder builder, Link link) {
        BlockPosition position = builder.getBlockPosition(link.position);
        return position == null ? "" : position.blockType().name();
    }


    private static boolean compatibleLinkName(SchematicsBuilder builder, Link link) {
        BlockPosition position = builder.getBlockPosition(link.position);
        if (position == null) return true;
        String baseName = position.blockType().getBaseLinkName();
        return link.name().startsWith(baseName) && link.name.substring(baseName.length()).matches("[1-9]\\d*");
    }

    private static String convertToMlog(SchematicsBuilder builder, AstProcessor processor, ProcessorType type,
            Map<String, String> schematicLinks, Map<String, String> symbolicNameMap) {
        return switch (processor.language()) {
            case NONE -> "";
            case MLOG -> processor.program().getProgramText(builder);
            case MINDCODE -> {
                String mindcode = processor.program().getProgramText(builder);
                String cached = builder.getMlogFromCache(mindcode);
                if (cached != null) {
                    yield cached;
                }

                InputFile fileToCompile = builder.getInputFiles().registerSource(mindcode);
                builder.addMessage(ToolMessage.info("%nCompiling %s", processor.program().getProgramId(builder)));
                CompilerProfile compilerProfile = builder.getCompilerProfile();
                compilerProfile.setPositionTranslator(processor.program().createPositionTranslator(builder));
                List<MindcodeMessage> messages = new ArrayList<>();
                Target schematicTarget = compilerProfile.getCompilerTarget().withType(type);
                MindcodeCompiler compiler = new MindcodeCompiler(messages::add,
                        compilerProfile.duplicate(true).setRun(false).setSchematicTarget(schematicTarget),
                        builder.getInputFiles());
                compiler.setSchematicLinks(schematicLinks);
                compiler.compile(fileToCompile);
                symbolicNameMap.putAll(compiler.getSymbolicNameMap());

                messages.forEach(builder::addMessage);
                if (messages.stream().anyMatch(MindcodeMessage::isError)) {
                    builder.error(processor.program(), "Compile errors in Mindcode source code.");
                    yield "";
                }

                String mlog = compiler.getOutput();
                builder.storeMlogToCache(mindcode, mlog);
                yield mlog;
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

        Link withName(String name) {
            return new Link(name, position);
        }
    }
}
