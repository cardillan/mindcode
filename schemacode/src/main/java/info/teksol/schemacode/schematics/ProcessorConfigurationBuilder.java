package info.teksol.schemacode.schematics;

import info.teksol.mc.async.AsyncExecutor;
import info.teksol.mc.common.InputFile;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.logic.opcodes.ProcessorType;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.options.Target;
import info.teksol.schemacode.ast.AstLink;
import info.teksol.schemacode.ast.AstProcessor;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.UnresolvedConfiguration;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NullMarked
public class ProcessorConfigurationBuilder {
    private final SchematicsBuilder builder;
    private boolean startAnnounced;

    public ProcessorConfigurationBuilder(SchematicsBuilder builder) {
        this.builder = builder;
    }

    private String getLinkBlockType(ProcessorConfiguration.Link link) {
        BlockPosition position = builder.getBlockPosition(link.position());
        return position == null ? "" : position.blockType().name();
    }

    public UnresolvedConfiguration fromAstConfiguration(AstProcessor processor, BlockPosition blockPos) {
        List<ProcessorConfiguration.Link> links = processorLinks(processor, blockPos);

        if (processor.language() == Language.MINDCODE) {
            return buildMindcodeConfiguration(processor, blockPos, links);
        } else {
            String mlog = processor.language() == Language.MLOG ? processor.program().getProgramText(builder) : "";
            return buildConfiguration(processor, links, mlog, Map.of());
        }
    }

    private List<ProcessorConfiguration.Link> processorLinks(AstProcessor processor, BlockPosition blockPos) {
        List<ProcessorConfiguration.Link> links = processor.links().stream()
                .mapMulti((AstLink l, Consumer<ProcessorConfiguration.Link> c) -> l.getProcessorLinks(c, builder, blockPos.position()))
                .distinct()
                .toList();

        // Detect link names used more than once
        Map<String, List<ProcessorConfiguration.Link>> linksByName = links.stream().collect(Collectors.groupingBy(ProcessorConfiguration.Link::name));
        linksByName.values().stream()
                .filter(v -> v.size() > 1)
                .forEachOrdered(l -> builder.error(processor, "Block link name '%s' used more than once.", l.getFirst().name()));

        // Detect blocks linked more than once
        Map<Position, List<ProcessorConfiguration.Link>> linksByPosition = links.stream().collect(Collectors.groupingBy(ProcessorConfiguration.Link::position));
        linksByPosition.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEachOrdered(l -> builder.error(processor, "Multiple links for block at position %s: '%s'.",
                        l.getKey().toStringAbsolute(),
                        l.getValue().stream().map(ProcessorConfiguration.Link::name).collect(Collectors.joining("', '"))));

        return links;
    }

    private ProcessorConfiguration buildConfiguration(AstProcessor processor,
            List<ProcessorConfiguration.Link> processorLinks, String mlog, Map<String, String> symbolicNameMap) {

        // Translate names
        List<ProcessorConfiguration.Link> links = processorLinks.stream()
                .map(link -> link.withName(symbolicNameMap.getOrDefault(link.name(), link.name()))).toList();

        // A set of names which were resolved by Mindcode and therefore have been type-checked already
        Set<String> resolvedNames = new HashSet<>(symbolicNameMap.values());

        Map<String, BitSet> usedNames = new HashMap<>();

        // Verify link names and numerical gaps
        // In consecutive sequence, the highest possible number is the number of links
        // Prevents the bitset size from exploding when huge numbers (e.g., 'switch9999999') are used
        final int maxValidNumber = links.size() + 1;
        for (ProcessorConfiguration.Link link : links) {
            BlockPosition position = builder.getBlockPosition(link.position());
            if (position == null) continue;

            String baseName = position.blockType().getBaseLinkName();
            String index = link.name().substring(Math.min(link.name().length(), baseName.length()));
            if (link.name().startsWith(baseName) && index.matches("[1-9]\\d{0,8}")) {
                int number = Integer.parseInt(index);  // Can't fail - see regex
                usedNames.computeIfAbsent(baseName, _ -> new BitSet()).set(Math.min(number, maxValidNumber));
            } else if (!resolvedNames.contains(link.name())) {
                // Do not report errors that have already been reported by Mindcode
                builder.error(processor, "Incompatible link name '%s' for block type '%s'.", link.name(),
                        builder.getBlockPosition(link.position()).blockType().name());
            }
        }

        usedNames.forEach((baseName, numbers) -> {
            int length = numbers.length();
            int firstUnused = numbers.nextClearBit(1);
            if (firstUnused < length) {
                builder.error(builder.getCompilerProfile().allowLinkGaps(), processor,
                        "Gaps in link numbers of block type '%s' (first missing link: '%s%d').",
                        baseName, baseName, firstUnused);
            }
        });
        return new ProcessorConfiguration(links, mlog);
    }

    private UnresolvedConfiguration buildMindcodeConfiguration(AstProcessor processor,
            BlockPosition blockPos, List<ProcessorConfiguration.Link> links) {
        // Gather expected link names
        Map<String, String> schematicLinks = links.stream()
                .collect(Collectors.toMap(ProcessorConfiguration.Link::name, this::getLinkBlockType));

        String mindcode = processor.program().getProgramText(builder);
        CompletableFuture<CompilerCacheEntry> compilation = getFromCompilerCache(mindcode);

        if (compilation == null) {
            if (!startAnnounced) {
                builder.addMessage(ToolMessage.info("\nCompiling Mindcode for schematics..."));
                startAnnounced = true;
            }

            // Set up a separate compiler environment
            InputFile fileToCompile = builder.getInputFiles().registerSource(mindcode);

            CompilerProfile compilerProfile = builder.getCompilerProfile()
                    .duplicate(true)
                    .setPositionTranslator(processor.program().createPositionTranslator(builder))
                    .setRun(false);

            ProcessorType processorType = ProcessorType.fromBlockType(blockPos.blockType());
            if (processorType != null) {
                Target schematicTarget = compilerProfile.getCompilerTarget().withType(processorType);
                compilerProfile.setSchematicTarget(schematicTarget);
            }

            // This is the async task
            compilation = AsyncExecutor.execute(() -> compile(compilerProfile, processor, schematicLinks, fileToCompile));

            storeToCompilerCache(mindcode, compilation);
        }

        return new FutureProcessorConfiguration(processor, links, compilation);
    }

    private CompilerCacheEntry compile(CompilerProfile compilerProfile, AstProcessor processor,
            Map<String, String> schematicLinks, InputFile fileToCompile) {

        List<MindcodeMessage> messages = new ArrayList<>();
        MindcodeCompiler compiler = new MindcodeCompiler(messages::add, compilerProfile, builder.getInputFiles());
        compiler.setSchematicLinks(schematicLinks);
        compiler.compile(fileToCompile);

        boolean hasErrors = messages.stream().anyMatch(MindcodeMessage::isError);
        synchronized (builder) {
            builder.addMessage(ToolMessage.info("%nFinished compiling %s", processor.program().getProgramId(builder)));
            messages.forEach(builder::addMessage);
            if (hasErrors) {
                builder.error(processor.program(), "Compile errors in Mindcode source code.");
            }
        }

        return new CompilerCacheEntry(compiler.getOutput(), compiler.getSymbolicNameMap(), hasErrors);
    }

    // Class responsible for constructing the final processor configuration whem Mindcode compilation completes
    private final class FutureProcessorConfiguration implements UnresolvedConfiguration {
        private final AstProcessor processor;
        private final List<ProcessorConfiguration.Link> links;
        private final CompletableFuture<CompilerCacheEntry> compilation;

        private FutureProcessorConfiguration(AstProcessor processor, List<ProcessorConfiguration.Link> links,
                CompletableFuture<CompilerCacheEntry> compilation) {
            this.processor = processor;
            this.links = links;
            this.compilation = compilation;
        }

        @Override
        public Configuration resolve() {
            try {
                CompilerCacheEntry result = compilation.get();
                return result.hasErrors ? EmptyConfiguration.EMPTY : buildConfiguration(processor, links, result.mlog(), result.symbolicNameMap());
            } catch (Exception ex) {
                throw new MindcodeInternalError(ex, "Error compiling Mindcode source code.");
            }
        }
    }


    public record CompilerCacheEntry(String mlog, Map<String, String> symbolicNameMap, boolean hasErrors) { }

    // Caches the results of compiling Mindcode to mlog - avoid repeated recompilation of identical mindcode
    // Maps the entire input string onto the output to avoid obtaining the wrong cached version
    private final Map<String, CompletableFuture<CompilerCacheEntry>> compilerCache = new HashMap<>();

    public @Nullable CompletableFuture<CompilerCacheEntry> getFromCompilerCache(String mindcode) {
        return compilerCache.get(mindcode);
    }

    public void storeToCompilerCache(String mindcode, CompletableFuture<CompilerCacheEntry> entry) {
        compilerCache.put(mindcode, entry);
    }
}
