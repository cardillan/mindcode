package info.teksol.mc.mindcode.tests;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;

import java.io.IOException;

@NullMarked
@Order(4)
public class ProcessorSymbolicLabelsTest extends ProcessorTestBase {

    @AfterAll
    static void done() throws IOException {
        AbstractProcessorTest.done(SCRIPTS_DIRECTORY, ProcessorSymbolicLabelsTest.class.getSimpleName());
    }
}
