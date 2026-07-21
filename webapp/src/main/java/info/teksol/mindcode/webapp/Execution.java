package info.teksol.mindcode.webapp;

import info.teksol.mc.common.Statistics;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.Instant;
import java.util.UUID;

@Table("executions")
@Immutable
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final long id;
    private final UUID sourceUuid;
    private final String type;
    private final String source;
    private final int nodeCount;
    private final int moduleCount;
    private final int unoptimized;
    private final int optimized;
    private final int parseTime;
    private final int compileTime;
    private final int optimizeTime;
    private final int runTime;
    private final int passes;
    private final int errorCount;
    private final int warningCount;

    @CreatedDate
    private final Instant createdAt;

    public Execution(UUID uuid, String type, String source, Statistics statistics) {
        this.id = 0;
        this.sourceUuid = uuid;
        this.type = type;
        this.source = source;
        this.nodeCount = statistics.nodeCount();
        this.moduleCount = statistics.moduleCount();
        this.unoptimized = statistics.unoptimized();
        this.optimized = statistics.optimized();
        this.parseTime = statistics.parseTime();
        this.compileTime = statistics.compileTime();
        this.optimizeTime = statistics.optimizeTime();
        this.runTime = statistics.runTime();
        this.passes = statistics.passes();
        this.errorCount = statistics.errorCount();
        this.warningCount = statistics.warningCount();
        this.createdAt = Instant.now();
    }

    public Execution(long id, UUID sourceUuid, String type, String source, int nodeCount, int moduleCount,
            int unoptimized, int optimized, int parseTime, int compileTime, int optimizeTime, int runTime,
            int passes, int errorCount, int warningCount, Instant createdAt) {
        this.id = id;
        this.sourceUuid = sourceUuid;
        this.type = type;
        this.source = source;
        this.nodeCount = nodeCount;
        this.moduleCount = moduleCount;
        this.unoptimized = unoptimized;
        this.optimized = optimized;
        this.parseTime = parseTime;
        this.compileTime = compileTime;
        this.optimizeTime = optimizeTime;
        this.runTime = runTime;
        this.passes = passes;
        this.errorCount = errorCount;
        this.warningCount = warningCount;
        this.createdAt = createdAt;
    }

    @PersistenceCreator
    public static Execution create(long id, UUID sourceUuid, String type, String source, int nodeCount, int moduleCount,
            int unoptimized, int optimized, int parseTime, int compileTime, int optimizeTime, int runTime,
            int passes, int errorCount, int warningCount, Instant createdAt) {
        return new Execution(id, sourceUuid, type, source, nodeCount, moduleCount, unoptimized, optimized,
                parseTime, compileTime, optimizeTime, runTime, passes, errorCount, warningCount, createdAt);
    }

    public long getId() {
        return id;
    }

    public UUID getSourceUuid() {
        return sourceUuid;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getModuleCount() {
        return moduleCount;
    }

    public int getUnoptimized() {
        return unoptimized;
    }

    public int getOptimized() {
        return optimized;
    }

    public int getParseTime() {
        return parseTime;
    }

    public int getCompileTime() {
        return compileTime;
    }

    public int getOptimizeTime() {
        return optimizeTime;
    }

    public int getRunTime() {
        return runTime;
    }

    public int getPasses() {
        return passes;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "id=" + id +
                ", sourceUuid=" + sourceUuid +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", nodeCount=" + nodeCount +
                ", moduleCount=" + moduleCount +
                ", unoptimized=" + unoptimized +
                ", optimized=" + optimized +
                ", parseTime=" + parseTime +
                ", compileTime=" + compileTime +
                ", optimizeTime=" + optimizeTime +
                ", runTime=" + runTime +
                ", passes=" + passes +
                ", errorCount=" + errorCount +
                ", warningCount=" + warningCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
