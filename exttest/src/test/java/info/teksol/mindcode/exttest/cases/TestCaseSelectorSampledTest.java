package info.teksol.mindcode.exttest.cases;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestCaseSelectorSampledTest {

    @Test
    void providesCorrectTestCaseNumbers() {
        TestCaseSelectorSampled instance = new TestCaseSelectorSampled(100,10);
        assertEquals(0, instance.getTestCaseNumber(0));
        assertEquals(10, instance.getTestCaseNumber(1));
        assertEquals(80, instance.getTestCaseNumber(8));
        assertEquals(90, instance.getTestCaseNumber(9));
    }

}