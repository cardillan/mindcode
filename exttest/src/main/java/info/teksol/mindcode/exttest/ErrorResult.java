package info.teksol.mindcode.exttest;

public record ErrorResult(int testNumber, String profile, String unexpectedMessages, String failedTests) {

    @Override
    public String toString() {
        return "Test #" + testNumber + ":"
               + "\n#set profile = " + profile + ";"
               + (unexpectedMessages.isEmpty() ? "" : "\nUnexpected messages: \n" + unexpectedMessages)
               + (failedTests.isEmpty() ? "" : "\nFailed tests: \n" + failedTests)
               + "\n";
    }
}
