package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NullMarked
public abstract class AbstractSegmentConfigurationGenerator implements SegmentConfigurationGenerator {

    static List<Partition> splitToPartitions(CaseExpression caseExpression, boolean handleNulls) {
        LogicLabel zeroTarget = caseExpression.get(0);
        if (handleNulls && zeroTarget != null) {
            // We need to handle zero separately because of possible null
            // Use INVALID as a placeholder
            caseExpression.addBranchKey(0, LogicLabel.INVALID);
        }

        List<Partition> partitions = new ArrayList<>();

        LogicLabel label = Objects.requireNonNull(caseExpression.firstLabel());
        int start = caseExpression.firstKey();
        int last = caseExpression.firstKey();

        // Else branch gaps
        for (Map.Entry<Integer, CaseExpression.Branch> entry : caseExpression.entrySet()) {
            int key = entry.getKey();

            if (key - last > 1) {
                // There's a gap, create a segment representing it.
                partitions.add(new Partition(start, last + 1, label));
                partitions.add(new Partition(last + 1, key, LogicLabel.EMPTY));
                label = entry.getValue().label;
                start = key;
            } else if (!label.equals(entry.getValue().label)) {
                partitions.add(new Partition(start, key, label));
                label = entry.getValue().label;
                start = key;
            }

            last = key;
        }

        partitions.add(new Partition(start, last + 1, label));

        if (zeroTarget != null) caseExpression.addBranchKey(0, zeroTarget);

        return List.copyOf(partitions);
    }
}
