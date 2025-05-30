package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class Targets {
    private final NavigableMap<Integer, LogicLabel> targets = new TreeMap<>();
    public @Nullable LogicLabel nullTarget;        // Handles null only
    public @Nullable LogicLabel elseTarget;        // Handles else only
    public @Nullable LogicLabel nullOrElseTarget;  // Handles else or null

    public boolean hasZeroKey() {
        return targets.containsKey(0);
    }

    public boolean hasNullKey() {
        return nullTarget != null;
    }

    public boolean hasNullOrZeroKey() {
        return nullTarget != null || targets.containsKey(0);
    }

    public @Nullable LogicLabel get(Integer key) {
        return targets.get(key);
    }

    public LogicLabel getExisting(Integer key) {
        return Objects.requireNonNull(targets.get(key));
    }

    public LogicLabel getOrDefault(Integer key, LogicLabel defaultValue) {
        return targets.getOrDefault(key, defaultValue);
    }

    public @Nullable LogicLabel put(@Nullable Integer key, LogicLabel value) {
        if (key == null) {
            LogicLabel previous = nullTarget;
            nullTarget = value;
            return previous;
        } else {
            return targets.put(key, value);
        }
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    public int size() {
        return targets.size();
    }

    public SortedMap<Integer, LogicLabel> subMap(int from, int to) {
        return targets.subMap(from, to);
    }

    public Map.Entry<Integer, LogicLabel> firstEntry() {
        return targets.firstEntry();
    }

    public Map.Entry<Integer, LogicLabel> lastEntry() {
        return targets.lastEntry();
    }

    public Integer firstKey() {
        return targets.firstKey();
    }

    public Integer lastKey() {
        return targets.lastKey();
    }

    public Set<Map.Entry<Integer, LogicLabel>> entrySet() {
        return targets.entrySet();
    }

    public Set<Integer> keySet() {
        return targets.keySet();
    }

    public LogicLabel getElseTarget() {
        return Objects.requireNonNull(elseTarget);
    }

    public LogicLabel getNullOrElseTarget() {
        return Objects.requireNonNull(nullOrElseTarget);
    }

    // Note: if the case expression doesn't contain a null branch, the nullTarget is set to the else branch.
    public LogicLabel getNullTarget() {
        return Objects.requireNonNull(nullTarget);
    }
}
