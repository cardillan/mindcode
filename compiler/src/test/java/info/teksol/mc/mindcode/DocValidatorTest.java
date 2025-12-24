package info.teksol.mc.mindcode;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.antlr.MindcodeLexer;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.mindcode.logic.mimex.BlockType;
import info.teksol.mc.mindcode.logic.mimex.ContentType;
import info.teksol.mc.mindcode.logic.mimex.MindustryMetadata;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.FileReferences;
import info.teksol.mc.profile.Remarks;
import info.teksol.mc.profile.options.*;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.Markdown;
import org.antlr.v4.runtime.Vocabulary;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
@Order(6)
public class DocValidatorTest {
    private static final String SYNTAX_REL_PATH = ".." + File.separatorChar + "doc" + File.separatorChar + "syntax" + File.separatorChar;
    private static final String OPTIMIZATIONS_PATH = SYNTAX_REL_PATH + "optimizations";
    private static final String OPTIONS_FILE = SYNTAX_REL_PATH + "SYNTAX-5-OTHER.markdown";
    private static final String OPTIMIZATIONS_FILE = SYNTAX_REL_PATH + "SYNTAX-6-OPTIMIZATIONS.markdown";

    private final String[] MAIN_SEQUENCE = {
            "SYNTAX-1-VARIABLES.markdown",
            "SYNTAX-2-EXPRESSIONS.markdown",
            "SYNTAX-3-STATEMENTS.markdown",
            "SYNTAX-4-FUNCTIONS.markdown",
            "FUNCTIONS.markdown",
            "SYSTEM-LIBRARY.markdown",
            "MINDUSTRY-8.markdown",
            "REMOTE-CALLS.markdown",
            "SYNTAX-5-OTHER.markdown",
            "SYNTAX-6-OPTIMIZATIONS.markdown",
            "SYNTAX-EXTENSIONS.markdown",
            "SCHEMACODE.markdown",
            "TOOLS-IDE-INTEGRATION.markdown",
            "TOOLS-CMDLINE.markdown",
            "TOOLS-PROCESSOR-EMULATOR.markdown",
            "TOOLS-MLOG-WATCHER.markdown",
            "TOOLS-REFRESHER.markdown",
            "TOOLS-MLOG-DECOMPILER.markdown",
            "TOOLS-TESTING-TOOL.markdown",
            "TROUBLESHOOTING.markdown",
            "PERFORMANCE-TIPS.markdown",
            "MINDUSTRY-TIPS-N-TRICKS.markdown",
    };

    private final String[] LIBRARY_SEQUENCE = {
            "SYSTEM-LIBRARY-ARRAYS.markdown",
            "SYSTEM-LIBRARY-BLOCKS.markdown",
            "SYSTEM-LIBRARY-COMPATIBILITY.markdown",
            "SYSTEM-LIBRARY-GRAPHICS.markdown",
            "SYSTEM-LIBRARY-MATH.markdown",
            "SYSTEM-LIBRARY-PRINTING.markdown",
            "SYSTEM-LIBRARY-UNITS.markdown",
    };

    private final Set<String> IGNORED_FILES = Set.of(
            "SYNTAX.markdown",
            "TUTORIAL-MINDCODE.markdown"
    );

    private CompilerProfile createCompilerProfile() {
        return CompilerProfile.fullOptimizations(false)
                .setRemarks(Remarks.COMMENTS)
                .setAutoPrintflush(false)
                .setSignature(false)
                .setRun(false);
    }

    private static void doesEqual(String expected, String actual, String message) {
        assertEquals(expected, actual, message);
    }

    @Test
    public void noUnknownFiles() {
        final File[] files = new File(SYNTAX_REL_PATH).listFiles((_, name) -> name.endsWith(".markdown"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one markdown file in " + SYNTAX_REL_PATH + "; found none");

        HashSet<String> fileNameSet = Arrays.stream(files).map(File::getName).collect(Collectors.toCollection(HashSet::new));
        fileNameSet.removeAll(IGNORED_FILES);
        Arrays.asList(LIBRARY_SEQUENCE).forEach(fileNameSet::remove);
        Arrays.asList(MAIN_SEQUENCE).forEach(fileNameSet::remove);
        fileNameSet.removeIf(s -> s.startsWith("FUNCTIONS-"));

        assertTrue(fileNameSet.isEmpty(), "Found unknown files: " + fileNameSet);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    public DynamicNode validateDocumentation() {
        final File[] files1 = new File(SYNTAX_REL_PATH).listFiles((_, name) -> name.endsWith(".markdown"));
        assertNotNull(files1);
        assertTrue(files1.length > 0, "Expected to find at least one markdown file in " + SYNTAX_REL_PATH + "; found none");

        final File[] files2 = new File(OPTIMIZATIONS_PATH).listFiles((_, name) -> name.endsWith(".markdown"));
        assertNotNull(files2);
        assertTrue(files2.length > 0, "Expected to find at least one markdown file in " + OPTIMIZATIONS_PATH + "; found none");

        return DynamicContainer.dynamicContainer(
                "Documentation validation",
                Stream.concat(Arrays.stream(files1), Arrays.stream(files2))
                        .map(file -> DynamicTest.dynamicTest("Validating " + file.getName(),
                                null, () -> validateFile(file))
                        )
        );
    }

    private void validateFile(File file) throws IOException {
        List<Executable> assertions = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());

        validateFileCode(file, lines, assertions);
        validateFileLists(file, lines, assertions);

        assertAll(assertions);
    }

    private void validateFileLists(File file, List<String> lines, List<Executable> assertions) {
        int start = 0;

        while (true) {
            int index = CollectionUtils.indexOf(lines, start, line -> line.trim().startsWith("<!--- list:"));
            if (index < 0) break;

            if (!lines.get(index + 1).isEmpty()) {
                assertions.add(() -> fail("Missing a blank line at " + uriString(file, index + 2)));
            }

            int closingIndex = CollectionUtils.indexOf(lines, index + 2, line -> !line.trim().startsWith("* "));
            if (closingIndex < 0) {
                assertions.add(() -> fail("Cannot determine the end of a list at " + uriString(file, index + 3)));
            }

            Collection<String> values = generateList(lines.get(index));
            if (values.isEmpty()) {
                assertions.add(() -> fail("Cannot determine the list type at " + uriString(file, index + 1)));
            } else {
                String expected = values.stream().sorted().map(v -> "* `" + v + "`").collect(Collectors.joining("\n"));
                String actual = String.join("\n", lines.subList(index + 2, closingIndex));
                assertions.add(() -> doesEqual(expected, actual, "Incorrect list at " + uriString(file, index + 3)));
            }

            start = closingIndex + 1;
        }
    }

    private Collection<String> generateList(String definition) {
        Pattern pattern = Pattern.compile("<!---\\s*list:([^\\s]+)");
        Matcher matcher = pattern.matcher(definition);

        if (!matcher.find()) return List.of();

        String[] split = matcher.group(1).split(":");

        return switch (split[0]) {
            case "blockNames" -> getBlockNames();
            case "blocks" -> getBlocks(split[1]);
            case "icons" -> getIcons(split[1]);
            case "keywords" -> getKeywords(MindcodeLexer.VOCABULARY);
            case "namedColors" -> getNamedColors();
            case "sounds" -> getSounds();
            default -> List.of();
        };
    }

    private Collection<String> getBlockNames() {
        return BlockType.getBaseLinkNames(MindustryMetadata.getLatest());
    }

    private Collection<String> getBlocks(String category) {
        return MindustryMetadata.getLatest().getAllBlocks().stream()
                .filter(b -> b.category().equals(category))
                .filter(b -> !b.visibility().equals("hidden"))
                .map(BlockType::name)
                .toList();
    }

    private Collection<String> getIcons(String category) {
        ContentType contentType = ContentType.valueOf(category.toUpperCase());
        String prefix = contentType == ContentType.UNKNOWN ? "" : contentType.name().toUpperCase() + "_";
        return MindustryMetadata.getLatest().getIcons().getContentIconsNames(contentType)
                .stream().map(n -> prefix + n.replace('-', '_').toUpperCase()).toList();
    }

    private Collection<String> getKeywords(Vocabulary vocabulary) {
        return IntStream.range(0, vocabulary.getMaxTokenType())
                .mapToObj(vocabulary::getLiteralName)
                .filter(l -> l != null && l.matches("'#?\\w+'"))
                .map(l -> l.substring(1, l.length() - 1))
                .filter(k -> !k.equals("elif") && !k.equals("elseif") && !k.startsWith("#"))
                .toList();
    }

    private Collection<String> getNamedColors() {
        return MindustryMetadata.getLatest().getColorNames();
    }

    private Collection<String> getSounds() {
        return MindustryMetadata.getLatest().getSoundNames();
    }

    private void validateFileCode(File file, List<String> lines, List<Executable> assertions) {
        int start = 0;

        while (true) {
            int index = CollectionUtils.indexOf(lines, start, line -> line.trim().equals("```Mindcode"));
            if (index < 0) break;

            int closingIndex = CollectionUtils.indexOf(lines, index + 1, line -> line.trim().equals("```"));
            if (closingIndex < 0) {
                assertions.add(() -> fail("Found nonterminated ```Mindcode block in " + uriString(file, index + 1)));
            }

            String source = String.join("\n", lines.subList(index + 1, closingIndex));

            int nextBlock = CollectionUtils.indexOf(lines, closingIndex + 1, line -> line.startsWith("```"));
            int closingNextBlock = nextBlock > 0 ? CollectionUtils.indexOf(lines, nextBlock + 1, line -> line.trim().equals("```")) : -1;
            String mlog = nextBlock >= 0 && lines.get(nextBlock).trim().startsWith("```mlog")
                    ? String.join("\n", lines.subList(nextBlock + 1, closingNextBlock))
                    : null;

            validateCode(assertions, file, index + 1, source, mlog);

            start = closingIndex + 1;
        }
    }

    private void validateCode(List<Executable> assertions, File file, int line, String source, @Nullable String mlog) {
        ListMessageLogger messageConsumer = new ListMessageLogger();
        InputFiles inputFiles = InputFiles.create();
        inputFiles.registerFile(file.toPath(), source);
        CompilerProfile profile = createCompilerProfile();
        MindcodeCompiler compiler = new MindcodeCompiler(messageConsumer, profile, inputFiles);
        try {
            compiler.compile();
        } catch (Exception error) {
            messageConsumer.addMessage(ToolMessage.error("Error compiling source code: " + error.getMessage()));
        }

        String message = messageConsumer.getMessages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .filter(m -> !m.message().matches("Variable '.*' is not used\\."))
                .filter(m -> !m.message().matches("Variable '.*' is not initialized\\."))
                .map(m -> m.formatMessage(sp -> sp.offsetLine(line).formatForIde(FileReferences.WINDOWS_URI)))
                .collect(Collectors.joining("\n"));

        assertions.add(() -> assertTrue(message.isEmpty(), "Found errors or warnings in " + uriString(file, line) + ":\n" + message));

        if (mlog != null) {
            String signature = "\nprint \"" + CompilerProfile.SIGNATURE + '"';
            String strippedMlog = mlog.endsWith(signature) ? mlog.substring(0, mlog.length() - signature.length()) : mlog;
            assertions.add(() -> assertEquals(strippedMlog, compiler.getOutput().trim(),
                    "Compiler output doesn't match mlog block in " + uriString(file, line)));
        }
    }

    private String uriString(File file, int line) {
        URI uri = file.toURI().normalize();
        return (System.getProperty("os.name").toLowerCase().startsWith("win")
                ? uri.toString().replaceAll("file:/", "file:///") : uri.toString())
                + ":" + line;
    }

    @Test
    public void validateOptionsDocumentation() throws IOException {
        File file = new File(OPTIONS_FILE);
        List<Executable> assertions = new ArrayList<>();
        List<String> fileContent = Files.readAllLines(file.toPath());

        for (OptionCategory optionCategory : OptionCategory.values()) {
            verifyOptionListInCategory(file, fileContent, optionCategory, assertions);
        }

        assertAll(assertions);
    }

    private void verifyOptionListInCategory(File file, List<String> fileContent, OptionCategory category, List<Executable> assertions) {
        Map<Enum<?>, CompilerOptionValue<?>> compilerOptions = CompilerOptionFactory.createCompilerOptions(false);

        List<CompilerOption> options = compilerOptions.values().stream()
                .map(CompilerOption.class::cast)
                .filter(option -> option.getCategory() == category && !HIDDEN_OPTIONS.contains(option.getOption()))
                .filter(option -> !(option.getOption() instanceof ExecutionFlag))
                .filter(option -> option.getAvailability() == OptionAvailability.UNIVERSAL || option.getAvailability() == OptionAvailability.DIRECTIVE)
                .toList();

        if (options.isEmpty()) return;

        options.stream()
                .filter(option -> !(option.getOption() instanceof Optimization))
                .forEach(option -> assertions.add(() -> {
                    String title = "### Option `" + option.getOptionName() + "`";
                    assertTrue(fileContent.contains(title), "Section " + title + " not found in " + file.getName());
                }));

        List<List<String>> rows = options.stream()
                .sorted(Comparator.comparingInt(this::getOptionPrecedence).thenComparing(CompilerOption::getOptionName))
                .map(option -> List.of(getOptionLink(option), option.getScope().name().toLowerCase(), option.getStability().name().toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));

        rows.addFirst(List.of("Option", "Scope", "Semantic stability"));

        String title = "## " + category.title;
        StringBuilder sb = new StringBuilder(title).append("\n\n").append(category.description).append("\n");

        Markdown.toMarkdownTable(rows, sb);
        String expected = sb.toString();

        int start = CollectionUtils.indexOf(fileContent, 0, title::equals);
        if (start < 0) {
            assertions.add(() -> doesEqual(expected, "","Section " + title + " not found in " + file.getName()));
            return;
        }

        int end = CollectionUtils.indexOf(fileContent, start + 1,
                line -> line.startsWith("##") || line.startsWith("**Option scope"));
        if (end < 0) {
            assertions.add(() -> doesEqual(expected, "","Section " + title + " empty in " + uriString(file, start + 1)));
            return;
        }

        String actual = IntStream.range(start, end).mapToObj(i -> fileContent.get(i).trim()).collect(Collectors.joining("\n"));

        assertions.add(() -> doesEqual(expected, actual,
                "Incorrect content of section " + title + " in " + uriString(file, start + 1)));
    }

    private String getOptionLink(CompilerOption option) {
        return switch (option.getOption()) {
            case Optimization _ ->
                    "[" + option.getOptionName() + "](" + "optimizations/" + option.getOptionName().toUpperCase() + ".markdown)";
//            case ExecutionFlag e -> "[" + option.getOptionName() + "](" + "#option-" + option.getOptionName() + ")";
            default -> "[" + option.getOptionName() + "](" + "#option-" + option.getOptionName() + ")";
        };
    }

    private static final Set<Enum<?>> HIDDEN_OPTIONS = Set.of(
            DebuggingOptions.CASE_CONFIGURATION,
            DebuggingOptions.DEBUG_OUTPUT
    );

    private int getOptionPrecedence(CompilerOption option) {
        return switch (option.getOption()) {
            case Optimization _, ExecutionFlag _ -> 1;
            default -> 0;
        };
    }

    @Test
    public void validateOptimizationsDocumentation() throws IOException {
        final String title = "## Individual Mindcode optimizations";

        File file = new File(OPTIMIZATIONS_FILE);
        List<Executable> assertions = new ArrayList<>();
        List<String> fileContent = Files.readAllLines(file.toPath());

        String expected = Stream.of(Optimization.values())
                .sorted(Comparator.comparing(Optimization::getName))
                .map(o -> String.format("* [%s](optimizations/%s.markdown): %s.", o.getName(), o.getOptionName().toUpperCase(),
                        o.getDescription().replace('\'', '`')))
                .collect(Collectors.joining("\n"));

        int start = CollectionUtils.indexOf(fileContent, 0, title::equals);
        int start2 = CollectionUtils.indexOf(fileContent, start + 1, line -> line.startsWith("*"));
        if (start < 0 || start2 < 0) {
            assertions.add(() -> doesEqual(expected, "","Section " + title + " not found in " + file.getName()));
            return;
        }

        int end = CollectionUtils.indexOf(fileContent, start2, line -> !line.startsWith("*"));
        if (end < 0) {
            assertions.add(() -> doesEqual(expected, "","Section " + title + " empty in " + uriString(file, start + 1)));
            return;
        }

        String actual = IntStream.range(start2, end).mapToObj(i -> fileContent.get(i).trim()).collect(Collectors.joining("\n"));

        assertions.add(() -> doesEqual(expected, actual,
                "Incorrect content of section " + title + " in " + uriString(file, start + 1)));

        assertAll(assertions);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    public DynamicNode validateMainSequenceLinks() {
        return validateSequenceLinks(MAIN_SEQUENCE, "[Up: Contents](SYNTAX.markdown)");
    }

    public DynamicNode validateSequenceLinks(String[] files, String upReference) {
        List<LinkFile> linkFiles = Stream.of(files)
                .map(file -> loadFile(SYNTAX_REL_PATH, file))
                .toList();

        return DynamicContainer.dynamicContainer(
                "Documentation validation",
                IntStream.range(0, linkFiles.size()).boxed()
                        .map(index -> DynamicTest.dynamicTest("Validating " + linkFiles.get(index).title,
                                null, () -> validateLinks(linkFiles, index, upReference))
                        )
        );
    }


    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    public DynamicNode validateOptimizationSequenceLinks() {
        final String upReference = "[Up: Code optimization](../SYNTAX-6-OPTIMIZATIONS.markdown)";
        List<LinkFile> linkFiles = Stream.of(Optimization.values())
                .sorted(Comparator.comparing(Optimization::getName))
                .map(o -> loadFile(OPTIMIZATIONS_PATH, o.getName(), o.getOptionName().toUpperCase() + ".markdown"))
                .toList();

        return DynamicContainer.dynamicContainer(
                "Documentation validation",
                        IntStream.range(0, linkFiles.size()).boxed()
                        .map(index -> DynamicTest.dynamicTest("Validating " + linkFiles.get(index).title,
                                null, () -> validateLinksAndTitle(linkFiles, index, upReference))
                        )
        );
    }

    private void validateLinks(List<LinkFile> linkFiles, int index, String upReference) {
        LinkFile linkFile = linkFiles.get(index);

        String expected = (index > 0 ? previous(linkFiles.get(index - 1)) : "") +
                upReference +
                (index < linkFiles.size() - 1 ? next(linkFiles.get(index + 1)) : "");

        int lineIndex = CollectionUtils.lastIndexOf(linkFile.lines, line -> !line.isBlank());
        String actual = linkFile.lines.get(lineIndex).trim();

        doesEqual(expected, actual, "Incorrect link in " + uriString(linkFile.file, lineIndex));
    }

    private void validateLinksAndTitle(List<LinkFile> linkFiles, int index, String upReference) {
        LinkFile f = linkFiles.get(index);
        assertAll(
                () -> doesEqual("# " + f.title, f.lines.getFirst(), "Incorrect title in " + uriString(f.file, 1)),
                () -> validateLinks(linkFiles, index, upReference)
        );
    }

    private String previous(LinkFile linkFile) {
        return String.format("[&#xAB; Previous: %s](%s) &nbsp; | &nbsp; ", linkFile.title, linkFile.fileName);
    }

    private String next(LinkFile linkFile) {
        return String.format(" &nbsp; | &nbsp; [Next: %s &#xBB;](%s)", linkFile.title, linkFile.fileName);
    }

    private record LinkFile(File file, String title, String fileName, List<String> lines) {
    }

    private static LinkFile loadFile(String directory, String title, String fileName) {
        try {
            File file = new File(directory, fileName);
            return new LinkFile(file, title, fileName, Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static LinkFile loadFile(String directory, String fileName) {
        try {
            File file = new File(directory, fileName);
            List<String> lines = Files.readAllLines(file.toPath());
            return new LinkFile(file, lines.getFirst().substring(2).trim(), fileName, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
