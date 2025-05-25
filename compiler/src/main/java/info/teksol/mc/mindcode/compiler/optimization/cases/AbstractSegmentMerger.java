package info.teksol.mc.mindcode.compiler.optimization.cases;

import info.teksol.mc.mindcode.logic.arguments.LogicLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractSegmentMerger implements SegmentMerger {

    static List<Segment> split(Targets targets, boolean logicConversion) {
        LogicLabel zeroTarget = targets.get(0);
        if (logicConversion && zeroTarget != null) {
            // We need to handle zero separately because of possible null
            // Use INVALID as a placeholder
            targets.put(0, LogicLabel.INVALID);
        }

        List<Segment> segments = new ArrayList<>();

        LogicLabel label = targets.firstEntry().getValue();
        int start = targets.firstKey();
        int last = targets.firstKey();

        // Else branch gaps
        for (Map.Entry<Integer, LogicLabel> entry : targets.entrySet()) {
            int key = entry.getKey();

            if (key - last > 1) {
                // There's a gap, create a segment representing it.
                segments.add(new Segment(SegmentType.SINGLE, start, last + 1, label));
                segments.add(new Segment(SegmentType.SINGLE, last + 1, key, LogicLabel.EMPTY));
                label = entry.getValue();
                start = key;
            } else if (!label.equals(entry.getValue())) {
                segments.add(new Segment(SegmentType.SINGLE, start, key, label));
                label = entry.getValue();
                start = key;
            }

            last = key;
        }

        segments.add(new Segment(SegmentType.SINGLE, start, last + 1, label));

        if (zeroTarget != null) targets.put(0, zeroTarget);

        return List.copyOf(segments);
    }
}
