package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.MindcodeException;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnStackTest {
    private final ReturnStack returnStack = new ReturnStack();

    private static final LogicLabel label1 = LogicLabel.symbolic("label1");
    private static final LogicLabel label2 = LogicLabel.symbolic("label2");

    private static final LogicVariable fnRetVal1 = LogicVariable.fnRetVal("fnRetVal1");
    private static final LogicVariable fnRetVal2 = LogicVariable.fnRetVal("fnRetVal2");

    @Test
    void remembersLabels() {
        returnStack.enterFunction(label1, fnRetVal1);

        assertEquals(label1, returnStack.getReturnLabel(null));
        assertEquals(fnRetVal1, returnStack.getReturnValue(null));
    }

    @Test
    void remembersMultipleLabels() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);

        assertEquals(label2, returnStack.getReturnLabel(null));
        assertEquals(fnRetVal2, returnStack.getReturnValue(null));
    }

    @Test
    void remembersMultipleLabelsAfterExit() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);
        returnStack.exitFunction();

        assertEquals(label1, returnStack.getReturnLabel(null));
        assertEquals(fnRetVal1, returnStack.getReturnValue(null));
    }

    @Test
    void rejectsProvidingValuesOnEmptyStack() {
        Assertions.assertThrows(MindcodeException.class, () -> returnStack.getReturnLabel(null));
        Assertions.assertThrows(MindcodeException.class, () -> returnStack.getReturnValue(null));
    }

    @Test
    void rejectsExitFunctionOnEmptyStack() {
        Assertions.assertThrows(IllegalStateException.class, returnStack::exitFunction);
    }
}
