package info.teksol.mindcode.compiler.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnStackTest {
    private final ReturnStack returnStack = new ReturnStack();

    @Test
    void remembersLabels() {
        returnStack.enterFunction("label", "retval");

        assertEquals("label", returnStack.getReturnLabel());
        assertEquals("retval", returnStack.getReturnValue());
    }

    @Test
    void remembersMultipleLabels() {
        returnStack.enterFunction("label1", "retval1");
        returnStack.enterFunction("label2", "retval2");

        assertEquals("label2", returnStack.getReturnLabel());
        assertEquals("retval2", returnStack.getReturnValue());
    }

    @Test
    void remembersMultipleLabelsAfterExit() {
        returnStack.enterFunction("label1", "retval1");
        returnStack.enterFunction("label2", "retval2");
        returnStack.exitFunction();

        assertEquals("label1", returnStack.getReturnLabel());
        assertEquals("retval1", returnStack.getReturnValue());
    }

    @Test
    void rejectsProvidingValuesOnEmptyStack() {
        Assertions.assertThrows(GenerationException.class, returnStack::getReturnLabel);
        Assertions.assertThrows(GenerationException.class, returnStack::getReturnValue);
    }

    @Test
    void rejectsExitFunctionOnEmptyStack() {
        Assertions.assertThrows(IllegalStateException.class, returnStack::exitFunction);
    }
}
