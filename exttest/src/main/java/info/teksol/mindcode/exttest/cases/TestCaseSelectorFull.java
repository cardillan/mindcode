package info.teksol.mindcode.exttest.cases;

public class TestCaseSelectorFull implements TestCaseSelector {

    @Override
    public int getTestCaseNumber(int testRunNumber) {
        return testRunNumber;
    }
}
