package info.teksol.mindcode.exttest.cases;

public class TestCaseSelectorFull implements TestCaseSelector {
    private final int totalCases;

    public TestCaseSelectorFull(int totalCases) {
        this.totalCases = totalCases;
    }

    @Override
    public int getTotalCases() {
        return totalCases;
    }

    @Override
    public int getSampleCount() {
        return totalCases;
    }

    @Override
    public int getTestCaseNumber(int testRunNumber) {
        return testRunNumber;
    }
}
