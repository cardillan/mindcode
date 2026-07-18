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
import info.teksol.mc.util.MutableInteger;
import info.teksol.schemacode.ast.*;
import info.teksol.schemacode.config.Configuration;
import info.teksol.schemacode.config.EmptyConfiguration;
import info.teksol.schemacode.config.UnresolvedConfiguration;
import info.teksol.schemacode.mindustry.Position;
import info.teksol.schemacode.mindustry.ProcessorConfiguration;
import info.teksol.schemacode.schematics.ParameterReplacer.ReplacementException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NullMarked
public class ProcessorConfigurationBuilder {
    private static final String MINDCODE = "mindcode";
    private static final String MLOG = "mlog";

    private final SchematicsBuilder builder;
    private boolean startAnnounced;

    public ProcessorConfigurationBuilder(SchematicsBuilder builder) {
        this.builder = builder;
    }

    private String getLinkBlockType(ProcessorConfiguration.Link link) {
        BlockPosition position = builder.getBlockPosition(link.position());
        return position == null ? "" : position.blockType().name();
    }

    public UnresolvedConfiguration fromAstConfiguration(AstProcessor processor, SchematicElement element) {
        List<ProcessorConfiguration.Link> links = processorLinks(processor, element);

        if (processor.language() == Language.MINDCODE) {
            return buildMindcodeConfiguration(processor, element, links);
        } else {
            String mlog = processor.language() == Language.MLOG ? processor.program().getProgramText(builder, MLOG) : "";
            return buildConfiguration(processor, links, mlog, Map.of(), Set.of());
        }
    }

    private List<ProcessorConfiguration.Link> processorLinks(AstProcessor processor, SchematicElement element) {
        for (AstLink link : processor.links()) {
            if (link instanceof AstLinkPos posLink && isGlobalLabelArray(posLink.name())) {
                builder.errorOnce(link, "Global label array '%s' is not supported as a link name in processor configuration.", posLink.name());
            }
        }

        List<ProcessorConfiguration.Link> links = processor.links().stream()
                .mapMulti((AstLink l, Consumer<ProcessorConfiguration.Link> consumer) -> l.getProcessorLinks(consumer, builder.configurationContext, element))
                .distinct()
                .toList();

        // Detect link names used more than once
        Map<String, List<ProcessorConfiguration.Link>> linksByName = links.stream().collect(Collectors.groupingBy(ProcessorConfiguration.Link::name));
        linksByName.values().stream()
                .filter(v -> v.size() > 1 && !isLabelArray(v.getFirst().name()))
                .forEachOrdered(l -> builder.error(processor, "Block link name '%s' used more than once.", l.getFirst().name()));

        // Detect blocks linked more than once
        Map<Position, List<ProcessorConfiguration.Link>> linksByPosition = links.stream().collect(Collectors.groupingBy(ProcessorConfiguration.Link::position));
        linksByPosition.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEachOrdered(l -> builder.error(processor, "Multiple links for block at position %s: '%s'.",
                        l.getKey().toStringAbsolute(),
                        l.getValue().stream().map(ProcessorConfiguration.Link::name).collect(Collectors.joining("', '"))));

        // Detect out-of-range links
        links.stream().filter(link -> !inRange(element, link))
                .forEachOrdered(l -> builder.error(processor, "Link at position (%d, %d) is out of range.", l.x(), l.y()));


        return links;
    }

    private boolean isGlobalLabelArray(@Nullable String label) {
        return label != null && !label.isEmpty() && label.charAt(label.length() - 1) == LayoutResolver.GLOBAL_LABEL_ARRAY_CHAR;
    }

    private boolean isLabelArray(@Nullable String label) {
        if (label == null || label.isEmpty()) return false;
        char ch = label.charAt(label.length() - 1);
        return ch == LayoutResolver.LOCAL_LABEL_ARRAY_CHAR || ch == LayoutResolver.GLOBAL_LABEL_ARRAY_CHAR;
    }

    private ProcessorConfiguration.Link resolveLabel(AstProcessor processor, Map<String, MutableInteger> arrayLabels, ProcessorConfiguration.Link link) {
        if (!isLabelArray(link.name())) return link;

        String label = link.name();
        MutableInteger current = arrayLabels.computeIfAbsent(label, _ -> MutableInteger.zero());
        return link.withName(label.substring(0, label.length() - 1) + current.incrementAndGet());
    }

    private ProcessorConfiguration buildConfiguration(AstProcessor processor, List<ProcessorConfiguration.Link> processorLinks,
            String mlog, Map<String, String> symbolicNameMap, Set<String> parameterNames) {

        Map<String, MutableInteger> arrayLabels = new HashMap<>();

        // Resolve label arrays and translate names
        List<ProcessorConfiguration.Link> links = processorLinks.stream()
                .map(l -> resolveLabel(processor, arrayLabels, l))
                .map(link -> link.withName(symbolicNameMap.getOrDefault(link.name(), link.name())))
                .toList();

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

        return new ProcessorConfiguration(links, processParametrization(processor, mlog, parameterNames));
    }

    private boolean inRange(SchematicElement processor, ProcessorConfiguration.Link link) {
        BlockPosition blockPosition = builder.getBlockPosition(link.position());
        int size = blockPosition == null ? 0 : blockPosition.blockType().size();

        float radius = processor.blockType().range() + size / 2f;
        float px = processor.position().x() + processor.blockType().size() / 2f;
        float py = processor.position().y() + processor.blockType().size() / 2f;
        float bx = link.x() + size / 2f;
        float by = link.y() + size / 2f;
        float x = bx - px;
        float y = by - py;
        return x * x + y * y < radius * radius;
    }

    private String processParametrization(AstProcessor processor, String mlog, Set<String> parameterNames) {
        Map<String, AstToken> tokenMap = new HashMap<>();
        Map<String, String> replacements = new HashMap<>();

        for (AstParameter parameter : processor.parameters()) {
            if (validateToken(parameter.name(), false) & validateToken(parameter.value(), true)) {
                if (replacements.put(parameter.name().tokenValue(), parameter.value().tokenValue()) != null) {
                    builder.error(parameter.name(), "The value of parameter '%s' is already defined.", parameter.name().tokenValue());
                } else if (processor.language() == Language.MINDCODE && !parameterNames.contains(parameter.name().tokenValue())) {
                    builder.error(parameter.name(), "Parameter '%s' is not defined in Mindcode.", parameter.name().tokenValue());
                } else {
                    tokenMap.put(parameter.name().tokenValue(), parameter.name());
                }
            }
        }

        if (replacements.isEmpty() || mlog.isEmpty()) return mlog;

        try {
            ParameterReplacer replacer = new ParameterReplacer(mlog, replacements);
            replacer.replace();
            replacer.getAbsentParameters().stream()
                    .map(tokenMap::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(AstToken::sourcePosition))
                    .forEach(token -> builder.error(token, "Parameter '%s' not found in the mlog code.", token.tokenValue()));
            return replacer.getResult();
        } catch (ReplacementException e) {
            builder.error(processor.program(), "Error trying to update program parameters: %s", e.getMessage());
            return mlog;
        }
    }

    private boolean validateToken(AstToken token, boolean allowString) {
        String str = token.tokenValue();
        boolean valid;
        if (str.startsWith("\"")) {
            valid = str.endsWith("\"") && str.indexOf('"', 1, str.length() - 1) == -1;
            if (valid && !allowString) {
                builder.error(token, "Parameter names cannot be strings.");
                return false;
            }
        } else {
            valid = str.indexOf('#') == -1 && str.indexOf(';') == -1;
        }

        if (!valid) {
            builder.error(token, "Invalid mlog token: %s.", str);
        }
        return valid;
    }

    private UnresolvedConfiguration buildMindcodeConfiguration(AstProcessor processor,
            BlockPosition blockPos, List<ProcessorConfiguration.Link> links) {
        // Gather expected link names
        Map<String, String> schematicLinks = links.stream()
                .collect(Collectors.toMap(ProcessorConfiguration.Link::name, this::getLinkBlockType));

        String mindcode = processor.program().getProgramText(builder, MINDCODE);
        CompletableFuture<CompilerCacheEntry> compilation = getFromCompilerCache(mindcode);

        if (compilation == null) {
            if (!startAnnounced) {
                builder.addMessage(ToolMessage.info("\nCompiling Mindcode for schematics..."));
                startAnnounced = true;
            }

            // Set up a separate compiler environment
            InputFile fileToCompile = builder.getInputFiles().registerSource(mindcode,
                    processor.program().createPositionTranslator(builder, MINDCODE));

            CompilerProfile compilerProfile = builder.getCompilerProfile()
                    .duplicate(true)
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
        compiler.safeCompile(fileToCompile);

        boolean hasErrors = messages.stream().anyMatch(MindcodeMessage::isError);
        synchronized (builder) {
            builder.addMessage(ToolMessage.info("%nFinished compiling %s", processor.program().getProgramId(builder, MINDCODE)));
            messages.forEach(builder::addMessage);
            if (hasErrors) {
                builder.error(processor.program(), "Compile errors in Mindcode source code.");
            }
        }

        return new CompilerCacheEntry(compiler.getOutput(), compiler.getSymbolicNameMap(), compiler.getParameterNames(), hasErrors);
    }

    // Class responsible for constructing the final processor configuration when Mindcode compilation completes
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
                return result.hasErrors ? EmptyConfiguration.EMPTY : buildConfiguration(processor, links, result.mlog(),
                        result.symbolicNameMap(), result.parameterNames());
            } catch (Exception ex) {
                throw new MindcodeInternalError(ex, "Error compiling Mindcode source code.");
            }
        }
    }


    public record CompilerCacheEntry(String mlog, Map<String, String> symbolicNameMap, Set<String> parameterNames,
                                     boolean hasErrors) {}

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
