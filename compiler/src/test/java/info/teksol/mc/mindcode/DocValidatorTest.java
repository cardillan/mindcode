package info.teksol.mc.mindcode;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.emulator.processor.ExecutionFlag;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.messages.ToolMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.MindcodeInternalError;
import info.teksol.mc.mindcode.compiler.optimization.Optimization;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.profile.FileReferences;
import info.teksol.mc.profile.Remarks;
import info.teksol.mc.profile.options.*;
import info.teksol.mc.util.CollectionUtils;
import info.teksol.mc.util.Markdown;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
@Order(6)
public class DocValidatorTest {
    private static final String SYNTAX_REL_PATH = ".." + File.separatorChar + "doc" + File.separatorChar + "syntax" + File.separatorChar;
    private static final String OPTIONS_FILE = SYNTAX_REL_PATH + File.separatorChar + "SYNTAX-5-OTHER.markdown";

    private CompilerProfile createCompilerProfile() {
        return CompilerProfile.fullOptimizations(false)
                .setRemarks(Remarks.COMMENTS)
                .setAutoPrintflush(false)
                .setSignature(false)
                .setRun(false);
    }

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    public DynamicNode validateDocumentation() {
        final File[] files = new File(SYNTAX_REL_PATH).listFiles((dir, name) -> name.endsWith(".markdown"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one markdown file in " + SYNTAX_REL_PATH + "; found none");

        return DynamicContainer.dynamicContainer(
                "Documentation validation",
                Stream.of(files)
                        .map(file -> DynamicTest.dynamicTest("Validating " + file.getName(),
                                null, () -> validateFile(file))
                        )
        );
    }

    private void validateFile(File file) throws IOException {
        List<Executable> assertions = new ArrayList<>();
        List<String> lines = Files.readAllLines(file.toPath());
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

        assertAll(assertions);
    }

    private void validateCode(List<Executable> assertions, File file, int line, String source, @Nullable String mlog) {
        ListMessageLogger messageConsumer = new ListMessageLogger();
        InputFiles inputFiles = InputFiles.create();
        inputFiles.registerFile(file.toPath(), source);
        CompilerProfile profile = createCompilerProfile();
        MindcodeCompiler compiler = new MindcodeCompiler(messageConsumer, profile, inputFiles);
        try {
            compiler.compile();
        } catch (MindcodeInternalError error) {
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
            assertions.add(() -> assertEquals(mlog, compiler.getOutput().trim(),
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
            assertions.add(() -> assertEquals(expected, "","Section " + title + " not found in " + file.getName()));
            return;
        }

        int end = CollectionUtils.indexOf(fileContent, start + 1,
                line -> line.startsWith("##") || line.startsWith("**Option scope"));
        if (end < 0) {
            assertions.add(() -> assertEquals(expected, "","Section " + title + " empty in " + uriString(file, start + 1)));
            return;
        }

        String actual = IntStream.range(start, end).mapToObj(i -> fileContent.get(i).trim()).collect(Collectors.joining("\n"));

        assertions.add(() -> assertEquals(expected, actual,
                "Incorrect content of section " + title + " in " + uriString(file, start + 1)));
    }

    private String getOptionLink(CompilerOption option) {
        return switch (option.getOption()) {
            case Optimization o ->
                    "[" + option.getOptionName() + "](" + "SYNTAX-6-OPTIMIZATIONS.markdown#" + option.getOptionName() + ")";
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
            case Optimization o -> 1;
            case ExecutionFlag o -> 1;
            default -> 0;
        };
    }
}
