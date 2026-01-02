package info.teksol.mindcode.exttest;

import info.teksol.mc.profile.CompilerProfile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ErrorResult(String testCaseId, CompilerProfile profile, int caseConfiguration,
                          String unexpectedMessages, String failedTests) {

    @Override
    public String toString() {
        return testCaseId + ":"
               + "\n#set profile = " + profile.encode() + ";"
               + (caseConfiguration < 0 ? "\n" : "\n#set case-configuration = " + caseConfiguration + ";")
               + (unexpectedMessages.isEmpty() ? "" : "\nUnexpected messages: \n" + unexpectedMessages)
               + (failedTests.isEmpty() ? "" : "\nFailed tests: \n" + failedTests)
               + "\n";
    }
}
