package info.teksol.mc.mindcode.compiler.generation;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import info.teksol.mc.mindcode.logic.arguments.LogicVariable;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NullMarked
public class ReturnStackTest {
    private final ReturnStack returnStack = new ReturnStack();

    private static final LogicLabel label1 = LogicLabel.symbolic("label1");
    private static final LogicLabel label2 = LogicLabel.symbolic("label2");

    private static final LogicVariable fnRetVal1 = LogicVariable.fnRetVal("foo", "fnRetVal1");
    private static final LogicVariable fnRetVal2 = LogicVariable.fnRetVal("foo", "fnRetVal2");

    @Test
    void remembersLabels() {
        returnStack.enterFunction(label1, fnRetVal1);

        assertEquals(label1, returnStack.getReturnLabel());
        assertEquals(fnRetVal1, Objects.requireNonNull(returnStack.getReturnRecord()).value());
    }

    @Test
    void remembersMultipleLabels() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);

        assertEquals(label2, returnStack.getReturnLabel());
        assertEquals(fnRetVal2, Objects.requireNonNull(returnStack.getReturnRecord()).value());
    }

    @Test
    void remembersMultipleLabelsAfterExit() {
        returnStack.enterFunction(label1, fnRetVal1);
        returnStack.enterFunction(label2, fnRetVal2);
        returnStack.exitFunction();

        assertEquals(label1, returnStack.getReturnLabel());
        assertEquals(fnRetVal1, Objects.requireNonNull(returnStack.getReturnRecord()).value());
    }

    @Test
    void rejectsProvidingValuesOnEmptyStack() {
        ReturnStack returnStack = new ReturnStack();
        assertNull(returnStack.getReturnRecord());
    }

    @Test
    void rejectsExitFunctionOnEmptyStack() {
        Assertions.assertThrows(IllegalStateException.class, returnStack::exitFunction);
    }
}
