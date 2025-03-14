package info.teksol.mc.mindcode;

import info.teksol.mc.common.InputFiles;
import info.teksol.mc.messages.ListMessageLogger;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;
import info.teksol.mc.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.mc.profile.CompilerProfile;
import info.teksol.mc.util.CollectionUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@NullMarked
@Order(6)
public class DocValidatorTest {
    private static final String SYNTAX_REL_PATH = ".." + File.separatorChar + "doc" + File.separatorChar + "syntax" + File.separatorChar;

    private CompilerProfile createCompilerProfile() {
        return new CompilerProfile(false, OptimizationLevel.EXPERIMENTAL)
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
                assertions.add(() -> fail("Found unterminated ```Mindcode``` block in " + file.getName() + " at line " + (index + 1)));
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
        InputFiles inputFiles = InputFiles.fromSource(source);
        CompilerProfile profile = createCompilerProfile();
        MindcodeCompiler compiler = new MindcodeCompiler(messageConsumer, profile, inputFiles);
        compiler.compile();

        String message = messageConsumer.getMessages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .filter(m -> !m.message().matches("Variable '.*' is not used\\."))
                .filter(m -> !m.message().matches("Variable '.*' is not initialized\\."))
                .map(m -> m.formatMessage(sp -> sp.offsetLine(line).formatForIde()))
                .collect(Collectors.joining("\n"));

        assertions.add(() -> assertTrue(message.isEmpty(), "Found errors or warnings in " + file.getName() + " at line " + line + ":\n" + message));

        if (mlog != null) {
            assertions.add(() -> assertEquals(mlog, compiler.getOutput().trim(),
                    "Compiler output doesn't match mlog block in " + file.getName() + " at line " + line));
        }
    }
}
