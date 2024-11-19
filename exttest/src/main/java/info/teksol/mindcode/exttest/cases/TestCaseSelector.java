package info.teksol.mindcode.exttest.cases;

public interface TestCaseSelector {

    /** Translates test run number to test case number */
    int getTestCaseNumber(int testRunNumber);
}
