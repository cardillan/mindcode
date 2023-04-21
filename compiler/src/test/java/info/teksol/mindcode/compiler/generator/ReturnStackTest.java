package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.logic.LogicArgument;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnStackTest {
    private final ReturnStack returnStack = new ReturnStack();

    LogicLabel label1 = LogicLabel.symbolic("label");
    LogicLabel label2 = LogicLabel.symbolic("label");

    LogicVariable fnRetVal1 = LogicVariable.fnRetVal("fnRetVal1");
    LogicVariable fnRetVal2 = LogicVariable.fnRetVal("fnRetVal2");

    @Test
    void remembersLabels() {
        returnStack.enterFunction(label1, fnRetVal1);

        assertEquals(label1, returnStack.getReturnLabel());
        assertEquals(fnRetVal1, returnStack.getReturnValue());
    }

    @Test
    void remembersMultipleLabels() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);

        assertEquals(label2, returnStack.getReturnLabel());
        assertEquals(fnRetVal2, returnStack.getReturnValue());
    }

    @Test
    void remembersMultipleLabelsAfterExit() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);
        returnStack.exitFunction();

        assertEquals(label1, returnStack.getReturnLabel());
        assertEquals(fnRetVal1, returnStack.getReturnValue());
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
