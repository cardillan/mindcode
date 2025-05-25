package info.teksol.mindcode.exttest.cases;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface TestCaseCreator {

    /// Returns the total number of cases
    int getTotalCases();

    /// Returns the number of samples to be processed by this selector
    int getSampleCount();

    /// Provides a test case executor for a given test run
    TestCaseExecutor createExecutor(int testRunNumber);
}
