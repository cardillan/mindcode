package info.teksol.mc.mindcode.compiler.optimization.cases;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;

@NullMarked
public interface SegmentMerger {
    List<Partition> getPartitions();

    Set<SegmentConfiguration> createSegmentConfigurations();

    int getConfigurationCount();
}
