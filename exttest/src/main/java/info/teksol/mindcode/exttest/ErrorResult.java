package info.teksol.mindcode.exttest;

import info.teksol.mc.emulator.processor.ExecutionException;
import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ErrorResult(String testCaseId, CompilerProfile profile, int caseConfiguration, String unexpectedMessages,
                          @Nullable ExecutionException executionException, String failedTests) {

    @Override
    public String toString() {
        return testCaseId + ":"
               + "\n#set profile = " + profile.encode() + ";"
               + (caseConfiguration < 0 ? "\n" : "\n#set case-configuration = " + caseConfiguration + ";")
               + (unexpectedMessages.isEmpty() ? "" : "\nUnexpected messages: \n" + unexpectedMessages)
               + (executionException == null ? "" : "\n" + executionException.getMessage())
               + (failedTests.isEmpty() ? "" : "\nFailed tests: \n" + failedTests)
               + "\n";
    }
}
