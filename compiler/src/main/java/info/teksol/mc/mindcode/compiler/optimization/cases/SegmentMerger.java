package info.teksol.mc.mindcode.compiler.optimization.cases;

import java.util.List;
import java.util.Set;

public interface SegmentMerger {
    int MINIMAL_SEGMENT_SIZE = 4;
    int MAX_EXCEPTIONS_WHEN = 2;
    int MAX_EXCEPTIONS_ELSE = 3;

    List<Segment> getSingularSegments();

    Set<SegmentConfiguration> createMergeConfigurations();

    int getConfigurationCount();
}
