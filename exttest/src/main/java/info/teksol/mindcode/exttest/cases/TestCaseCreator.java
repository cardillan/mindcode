package info.teksol.mindcode.exttest.cases;

import info.teksol.mc.common.InputFile;
import info.teksol.mc.mindcode.compiler.MindcodeCompiler;

public interface TestCaseCreator {

    /** Returns the total number of cases */
    int getTotalCases();

    /** Returns the number of samples to be processed by this selector */
    int getSampleCount();

    /** Creates and configures the compiler for given test run */
    MindcodeCompiler createCaseCompiler(int testRunNumber);

    /** Provides an input file for given test run */
    InputFile getInputFile(int testRunNumber);

    String getTestCaseId(int testRunNumber);
}
