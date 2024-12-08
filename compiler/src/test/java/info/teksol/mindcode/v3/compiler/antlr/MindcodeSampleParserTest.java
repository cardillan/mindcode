package info.teksol.mindcode.v3.compiler.antlr;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
public class MindcodeSampleParserTest extends AbstractParserTest {

    public static final String SCRIPTS_DIRECTORY = "src/test/resources/info/teksol/emulator/processor/optimizer";

    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    DynamicNode runScripts() {
        final File[] files = new File(SCRIPTS_DIRECTORY).listFiles((dir, name) -> name.endsWith(".mnd"));
        assertNotNull(files);
        assertTrue(files.length > 0, "Expected to find at least one script in " + SCRIPTS_DIRECTORY + "; found none");

        return DynamicContainer.dynamicContainer("Samples parse tests",
                Stream.of(files)
                        .map(File::getName)
                        .map(f -> DynamicTest.dynamicTest(f, null, () -> parseFile(f)))
        );
    }

    private void parseFile(String filename) throws IOException {
        Path path = Path.of(SCRIPTS_DIRECTORY, filename);
        assertParses(Files.readString(path));
    }
}
