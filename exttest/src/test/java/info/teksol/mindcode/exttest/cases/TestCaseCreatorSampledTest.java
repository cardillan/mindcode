package info.teksol.mindcode.exttest.cases;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestCaseCreatorSampledTest {

    @Test
    void providesCorrectTestCaseNumbers() {
        TestCaseCreatorSampled instance = new TestCaseCreatorSampled(null, 100, 10);
        assertEquals(5, instance.getTestCaseNumber(0));
        assertEquals(15, instance.getTestCaseNumber(1));
        assertEquals(85, instance.getTestCaseNumber(8));
        assertEquals(95, instance.getTestCaseNumber(9));
    }

}