package info.teksol.mindcode.processor;

import info.teksol.mindcode.compiler.AbstractGeneratorTest;
import info.teksol.mindcode.compiler.CompilerProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Base class for algorithm tests
// Processor for execution is equipped with bank1 memory bank.
// Additional blocks can be added
public class AbstractProcessorTest extends AbstractGeneratorTest {

    // Prevent unit tests hanging due to possible endless loops in generated code
    protected final int MAX_STEPS = 1000000;

    private TestInfo testInfo;

    @BeforeEach
    void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    protected String readFile(String filename) throws IOException {
        Path path = Path.of("src/test/resources/testcode", filename);
        return Files.readString(path);
    }

    protected CompilerProfile getCompilerProfile() {
        return CompilerProfile.standardOptimizations();
    }

    protected void testAndEvaluateCode(String code, List<MindustryObject> blocks, Consumer<List<String>> evaluator) {
        Processor processor = new Processor();
        processor.addBlock(MindustryMemory.createMemoryBank("bank1"));
        blocks.forEach(processor::addBlock);
        processor.run(compile(code), MAX_STEPS);
        System.out.printf("Test %-40s %6d steps%n", testInfo.getDisplayName() + ":", processor.getSteps());
        //System.out.println(String.join("", processor.getTextBuffer()));
        evaluator.accept(processor.getTextBuffer());
    }

    protected void testCode(String code, List<MindustryObject> blocks, List<String> expectedOutputs) {
        testAndEvaluateCode(code, blocks, outputs -> assertEquals(expectedOutputs, outputs));
    }

    protected void testCode(String code, String... expectedOutputs) {
        testCode(code, List.of(), List.of(expectedOutputs));
    }

    protected void testFile(String fileName, List<MindustryObject> blocks, List<String> expectedOutputs) throws IOException {
        testCode(readFile(fileName), blocks, expectedOutputs);
    }

    protected void testFile(String fileName, String... expectedOutputs) throws IOException {
        testFile(fileName, List.of(), List.of(expectedOutputs));
    }

}
