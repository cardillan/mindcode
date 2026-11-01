package io.github.cardillan.mc.emulator;

import io.github.cardillan.mc.evaluator.ConditionEvaluator;
import io.github.cardillan.mc.messages.MessageLevel;
import io.github.cardillan.mc.messages.MindcodeMessage;
import io.github.cardillan.mc.mindcode.logic.arguments.Condition;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record BasicAssertion(Condition condition, LVar x, LVar y, String title) implements Assertion {
    @Override
    public boolean success() {
        return Objects.requireNonNull(ConditionEvaluator.getCondition(condition)).evaluate(x, y);
    }

    @Override
    public MindcodeMessage createMessage() {
        return new EmulatorMessage(success() ? MessageLevel.INFO : MessageLevel.ERROR,
                null, -1, null,
                String.format("""
                                %s "%s" (%s):
                                    Expected : true (%s %s %s)
                                    Actual   : false""",
                        getClass().getSimpleName(), title(),
                        success() ? "success" : "failure", x.printExact(), condition.getMindcode(), y.printExact()));
    }

    @Override
    public String generateErrorMessage() {
        return "Failed test " + title() + ": " + x.printExact() + " " + condition.getMindcode() + " " + y.printExact() + " is false.";
    }
}
