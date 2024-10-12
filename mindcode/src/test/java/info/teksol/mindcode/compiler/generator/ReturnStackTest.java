package info.teksol.mindcode.compiler.generator;

import info.teksol.mindcode.compiler.ExpectedMessages;
import info.teksol.mindcode.logic.LogicLabel;
import info.teksol.mindcode.logic.LogicVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static info.teksol.mindcode.InputPosition.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReturnStackTest {
    private final ReturnStack returnStack = new ReturnStack(ExpectedMessages.refuseAll());

    private static final LogicLabel label1 = LogicLabel.symbolic("label1");
    private static final LogicLabel label2 = LogicLabel.symbolic("label2");

    private static final LogicVariable fnRetVal1 = LogicVariable.fnRetVal("fnRetVal1");
    private static final LogicVariable fnRetVal2 = LogicVariable.fnRetVal("fnRetVal2");

    @Test
    void remembersLabels() {
        returnStack.enterFunction(label1, fnRetVal1);

        assertEquals(label1, returnStack.getReturnLabel(EMPTY));
        assertEquals(fnRetVal1, returnStack.getReturnValue(EMPTY));
    }

    @Test
    void remembersMultipleLabels() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);

        assertEquals(label2, returnStack.getReturnLabel(EMPTY));
        assertEquals(fnRetVal2, returnStack.getReturnValue(EMPTY));
    }

    @Test
    void remembersMultipleLabelsAfterExit() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);
        returnStack.exitFunction();

        assertEquals(label1, returnStack.getReturnLabel(EMPTY));
        assertEquals(fnRetVal1, returnStack.getReturnValue(EMPTY));
    }

    @Test
    void rejectsProvidingValuesOnEmptyStack() {
        ExpectedMessages.create()
                .add("Return statement outside of a function.")
                .add("Return statement outside of a function.")
                .validate(consumer -> {
                    ReturnStack returnStack = new ReturnStack(consumer);
                    returnStack.getReturnLabel(EMPTY);
                    returnStack.getReturnValue(EMPTY);
                });
    }

    @Test
    void rejectsExitFunctionOnEmptyStack() {
        Assertions.assertThrows(IllegalStateException.class, returnStack::exitFunction);
    }
}
