package info.teksol.mindcode.exttest.cases;

public class TestCaseSelectorSampled implements TestCaseSelector {
    private final long caseCount;
    private final long sampleCount;

    public TestCaseSelectorSampled(int caseCount, int sampleCount) {
        this.caseCount = caseCount;
        this.sampleCount = sampleCount;
    }

    @Override
    public int getTestCaseNumber(int testRunNumber) {
        return (int) ((caseCount * testRunNumber) / sampleCount);
    }
}
