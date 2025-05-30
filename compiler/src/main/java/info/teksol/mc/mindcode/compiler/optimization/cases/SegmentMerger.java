package info.teksol.mc.mindcode.compiler.optimization.cases;

import java.util.List;
import java.util.Set;

public interface SegmentMerger {
    List<Partition> getPartitions();

    Set<SegmentConfiguration> createSegmentConfigurations();

    int getConfigurationCount();
}
