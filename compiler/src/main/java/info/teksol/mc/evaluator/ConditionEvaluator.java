package info.teksol.mc.evaluator;

import info.teksol.mc.mindcode.logic.arguments.Condition;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class ConditionEvaluator {

    public static @Nullable LogicCondition getCondition(@Nullable Condition condition) {
        return condition == null ? null : CONDITIONS.get(condition);
    }

    private static final Map<Condition, LogicCondition> CONDITIONS = createConditionsMap();

    private static Map<Condition, LogicCondition> createConditionsMap() {
        Map<Condition, LogicCondition> map = new HashMap<>();
        map.put(Condition.EQUAL,           ExpressionEvaluator::equals);
        map.put(Condition.NOT_EQUAL,       (a,  b) -> !ExpressionEvaluator.equals(a, b));
        map.put(Condition.LESS_THAN,       (a,  b) -> a.getDoubleValue() <  b.getDoubleValue());
        map.put(Condition.LESS_THAN_EQ,    (a,  b) -> a.getDoubleValue() <= b.getDoubleValue());
        map.put(Condition.GREATER_THAN,    (a,  b) -> a.getDoubleValue() >  b.getDoubleValue());
        map.put(Condition.GREATER_THAN_EQ, (a,  b) -> a.getDoubleValue() >= b.getDoubleValue());
        map.put(Condition.STRICT_EQUAL,    (a,  b) -> a.isObject() == b.isObject() && ExpressionEvaluator.strictlyEquals(a, b));
        map.put(Condition.ALWAYS,          (a,  b) -> true);
        return map;
    }

}
