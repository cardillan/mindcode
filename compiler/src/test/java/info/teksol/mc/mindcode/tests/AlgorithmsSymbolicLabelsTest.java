package info.teksol.mc.mindcode.tests;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;

import java.io.IOException;

@NullMarked
@Order(5)
public class AlgorithmsSymbolicLabelsTest extends AlgorithmsTestBase {

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, AlgorithmsSymbolicLabelsTest.class.getSimpleName());
    }
}
