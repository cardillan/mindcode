package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.CompilerProfile;

public record ErrorResult(int testCaseNumber, CompilerProfile profile, String unexpectedMessages, String failedTests) {

    @Override
    public String toString() {
        return "Test #" + testCaseNumber + ":"
               + "\n#set profile = " + profile.encode() + ";"
               + (unexpectedMessages.isEmpty() ? "" : "\nUnexpected messages: \n" + unexpectedMessages)
               + (failedTests.isEmpty() ? "" : "\nFailed tests: \n" + failedTests)
               + "\n";
    }
}
