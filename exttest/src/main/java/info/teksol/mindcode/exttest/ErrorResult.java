package info.teksol.mindcode.exttest;

import info.teksol.emulator.processor.ExecutionException;
import info.teksol.mindcode.compiler.CompilerProfile;

public record ErrorResult(String testCaseId, CompilerProfile profile, String unexpectedMessages,
                          ExecutionException executionException, String failedTests) {

    @Override
    public String toString() {
        return testCaseId + ":"
               + "\n#set profile = " + profile.encode() + ";"
               + (unexpectedMessages.isEmpty() ? "" : "\nUnexpected messages: \n" + unexpectedMessages)
               + (executionException == null ? "" : "\n" + executionException.getMessage())
               + (failedTests.isEmpty() ? "" : "\nFailed tests: \n" + failedTests)
               + "\n";
    }
}
