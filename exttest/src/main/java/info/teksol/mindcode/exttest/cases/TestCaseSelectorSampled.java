package info.teksol.mindcode.exttest.cases;

public class TestCaseSelectorSampled implements TestCaseSelector {
    private final long firstCase;
    private final long totalCases;
    private final long sampleCount;

    public TestCaseSelectorSampled(int totalCases, int sampleCount) {
        this.totalCases = totalCases;
        this.sampleCount = sampleCount;
        firstCase = totalCases / sampleCount / 2;
    }

    @Override
    public int getTotalCases() {
        return (int) totalCases;
    }

    @Override
    public int getSampleCount() {
        return (int) sampleCount;
    }

    @Override
    public int getTestCaseNumber(int testRunNumber) {
        return (int) (firstCase + (totalCases * testRunNumber) / sampleCount);
    }
}
