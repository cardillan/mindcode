package info.teksol.mindcode.exttest.cases;

import info.teksol.mindcode.exttest.TestProgress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TestCaseExecutor {
    void runTest(TestProgress progress);
}
