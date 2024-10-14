package info.teksol.mindcode.compiler;

import info.teksol.mindcode.InputFile;
import info.teksol.mindcode.MindcodeMessage;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import info.teksol.util.CollectionUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Order(99)
class MindcodeCompilerTest {

    MindcodeCompiler compiler = new MindcodeCompiler(
            new CompilerProfile(false, OptimizationLevel.ADVANCED)
                    .setFinalCodeOutput(FinalCodeOutput.PLAIN)
                    .setRun(true)
    );

    @Test
    public void producesAllOutputs() {
        CompilerOutput<String> result = compiler.compile(InputFile.createSourceFiles("""
                remark("This is a parameter");
                param value = true;
                if value then
                    print("Hello");
                end;
                """));

        assertEquals("""
                jump 2 always 0 0
                print "This is a parameter"
                set value true
                jump 0 equal value false
                print "Hello"
                """,
                result.output());

        assertEquals("Hello", result.textBuffer());

        int index = CollectionUtils.findFirstIndex(result.messages(),
                m -> m.message().contains("Final code before resolving virtual instructions"));
        assertTrue(index >= 0, "Failed to locate code output in the log.");

        MindcodeMessage message = result.messages().get(index + 1);

        // Indenting is crucial here
        assertEquals("""
                        label __start__
                    0:  remark "This is a parameter"
                    2:  set value true
                    3:  jump __start__ equal value false
                    4:  print "Hello"
                """, message.message());
    }

    @Test
    public void handlesMultipleFiles() {
        InputFile file1 = new InputFile("file1.mnd", "file1.mnd", "print(\"File1\");");
        InputFile file2 = new InputFile("file2.mnd", "file2.mnd", "print(\"File2\");");

        CompilerOutput<String> result = compiler.compile(List.of(file1, file2));

        assertEquals("""
                print "File2File1"
                """,
                result.output());

        assertEquals("File2File1", result.textBuffer());

        int index = CollectionUtils.findFirstIndex(result.messages(),
                m -> m.message().contains("Final code before resolving virtual instructions"));
        assertTrue(index >= 0, "Failed to locate code output in the log.");

        MindcodeMessage message = result.messages().get(index + 1);

        // Indenting is crucial here
        assertEquals("""
                    0:  print "File2File1"
                """, message.message());
    }

    @Test
    public void compilesAllSysFunctions() {
        InputFile source = MindcodeCompiler.loadLibraryFromResource("sys");

        String initializations = """
                #set target = ML8A;
                SYS_MESSAGE = null;
                """;

        String variables = source.code().lines()
                .filter(line -> line.startsWith("def "))
                .flatMap(MindcodeCompilerTest::extractVariables)
                .distinct()
                .sorted()
                .map(s -> s + " = null;")
                .collect(Collectors.joining("\n"));

        String functionCalls = source.code().lines()
                .filter(line -> line.startsWith("def "))
                .map(line -> line.substring(4) + ";")
                .collect(Collectors.joining("\n"));

        // We know there must be a variable names display
        String code = initializations + "\n" + variables + "\n" + functionCalls;

        CompilerOutput<String> result = compiler.compile(InputFile.createSourceFiles(code));

        String messages = result.messages().stream()
                .filter(MindcodeMessage::isErrorOrWarning)
                .map(MindcodeMessage::message)
                .collect(Collectors.joining("\n"));

        if (!messages.isEmpty()) {
            fail("Unexpected error or warning messages were generated:\n" + messages);
        }
    }

    private static Stream<String> extractVariables(String declaration) {
        int start = declaration.indexOf('(');
        int end = declaration.indexOf(')');
        return start < end - 1
                ? Arrays.stream(declaration.substring(start + 1, end).split(",")).map(String::trim)
                : Stream.empty();
    }
}
