package info.teksol.mc.emulator;

import info.teksol.mc.evaluator.ConditionEvaluator;
import info.teksol.mc.messages.MessageLevel;
import info.teksol.mc.messages.MindcodeMessage;
import info.teksol.mc.mindcode.logic.arguments.Condition;
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
