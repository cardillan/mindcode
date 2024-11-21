package info.teksol.mindcode.exttest.cases;

public interface TestCaseSelector {

    /** Returns the total number of cases */
    int getTotalCases();

    /** Returns the number of samples to be processed by this selector */
    int getSampleCount();

    /** Translates test run number to test case number */
    int getTestCaseNumber(int testRunNumber);
}
