package io.github.cardillan.mindcode.compiler.optimization.cases;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;

@NullMarked
public interface SegmentConfigurationGenerator {
    List<Partition> getPartitions();

    Set<SegmentConfiguration> createSegmentConfigurations();
}
