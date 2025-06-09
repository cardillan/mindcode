package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NullMarked
public abstract class AbstractSegmentMerger implements SegmentMerger {

    static List<Partition> splitToPartitions(Targets targets, boolean logicConversion) {
        LogicLabel zeroTarget = targets.get(0);
        if (logicConversion && zeroTarget != null) {
            // We need to handle zero separately because of possible null
            // Use INVALID as a placeholder
            targets.put(0, LogicLabel.INVALID);
        }

        List<Partition> partitions = new ArrayList<>();

        LogicLabel label = targets.firstEntry().getValue();
        int start = targets.firstKey();
        int last = targets.firstKey();

        // Else branch gaps
        for (Map.Entry<Integer, LogicLabel> entry : targets.entrySet()) {
            int key = entry.getKey();

            if (key - last > 1) {
                // There's a gap, create a segment representing it.
                partitions.add(new Partition(start, last + 1, label));
                partitions.add(new Partition(last + 1, key, LogicLabel.EMPTY));
                label = entry.getValue();
                start = key;
            } else if (!label.equals(entry.getValue())) {
                partitions.add(new Partition(start, key, label));
                label = entry.getValue();
                start = key;
            }

            last = key;
        }

        partitions.add(new Partition(start, last + 1, label));

        if (zeroTarget != null) targets.put(0, zeroTarget);

        return List.copyOf(partitions);
    }
}
