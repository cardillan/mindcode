package io.github.cardillan.mindcode.exttest.cases;

import io.github.cardillan.mindcode.exttest.TestProgress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TestCaseExecutor {
    void runTest(TestProgress progress);
}
