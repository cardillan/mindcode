package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.GenerationGoal;
import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationLevel;
import org.intellij.lang.annotations.PrintFormat;

import java.io.PrintWriter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class BasicTestProgress extends AbstractTestProgress {
    private final Map<Optimization, Map<OptimizationLevel, AtomicInteger>> statistics = new EnumMap<>(Optimization.class);
    private final Map<GenerationGoal, AtomicInteger> goalStatistics = new EnumMap<>(GenerationGoal.class);

    private final TestConfiguration configuration;
    private final int samples;
    private final long start = System.nanoTime();

    private long lastTime = System.nanoTime();
    private int lastCount = 0;
    private long lastGcTime = 0;
    private long lastGcRuns = 0;

    public BasicTestProgress(TestConfiguration configuration) {
        this.configuration = configuration;
        this.samples = configuration.getTestCaseCreator().getSampleCount();

        for (Optimization optimization : Optimization.LIST) {
            Map<OptimizationLevel, AtomicInteger> levels = new EnumMap<>(OptimizationLevel.class);
            for (OptimizationLevel optimizationLevel : OptimizationLevel.values()) {
                levels.put(optimizationLevel, new AtomicInteger(0));
            }
            statistics.put(optimization, levels);
        }

        Stream.of(GenerationGoal.values()).forEach(g -> goalStatistics.put(g, new AtomicInteger(0)));
    }

    @Override
    public boolean finished() {
        return finishedCount.get() >= samples || errorCount.get() > configuration.getFailureLimit();
    }

    @Override
    public void processResults(PrintWriter writer) {
        ErrorResult errorResult;
        while ((errorResult = errors.poll()) != null) {
            writer.println(errorResult);
            updateStatistics(errorResult);
        }
        writer.flush();

    }

    @Override
    public void updateStatistics(ErrorResult errorResult) {
        goalStatistics.get(errorResult.profile().getGoal()).incrementAndGet();
        for (Optimization optimization : Optimization.values()) {
            statistics.get(optimization).get(errorResult.profile().getOptimizationLevel(optimization)).incrementAndGet();
        }
    }

    @Override
    public void printProgress(boolean finished) {
        if (finished && lastCount == finishedCount.get()) {
            return;
        }

        long time = System.nanoTime();
        long elapsed = time - start;
        int count = finishedCount.get();
        int errors = errorCount.get();
        double rate = (count - lastCount) * 1_000_000_000d / (time - lastTime);
        double totalRate = count * 1_000_000_000d / elapsed;
        int remaining = (int) ((samples - count) / totalRate);

        final double mb = 1024 * 1024;
        final double free = Runtime.getRuntime().freeMemory() / mb;
        final double total = Runtime.getRuntime().totalMemory() / mb;
        final double max = Runtime.getRuntime().maxMemory() / mb;
        final double used = total - free;

        List<GarbageCollectorMXBean> gcCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long gcRuns = 0;
        long gcTime = 0;
        for (GarbageCollectorMXBean gcBean : gcCollectorMXBeans) {
            gcRuns += gcBean.getCollectionCount();
            gcTime += gcBean.getCollectionTime();
        }

        System.out.printf("Tests: %,d/%,d (%.1f%% done), failures: %,d (rate %.2f%%), speed: %7.1f tests/s, elapsed %s, remaining: %s" +
                          "    memory size %,5d MB, GC: %+,d runs, %+.3f s%n",
                count, samples, 100d * count / samples, errors, 100d * errors / count, rate,
                formatTime((int) (elapsed / 1_000_000_000d)), formatTime(remaining),
                (int) used, (gcRuns - lastGcRuns), (gcTime - lastGcTime) / 1000.0);

        lastGcRuns = gcRuns;
        lastGcTime = gcTime;
        lastCount = count;
        lastTime = time;
    }

    @Override
    public void printFinalMessage(PrintWriter writer) {
        long elapsed = System.nanoTime() - start;
        int count = finishedCount.get();
        int errors = errorCount.get();
        double totalRate = count * 1_000_000_000d / elapsed;

        List<GarbageCollectorMXBean> gcCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long gcRuns = 0;
        long gcTime = 0;
        for (GarbageCollectorMXBean gcBean : gcCollectorMXBeans) {
            gcRuns += gcBean.getCollectionCount();
            gcTime += gcBean.getCollectionTime();
        }

        printf(writer, "%n");
        printf(writer, "Finished.%n");
        printf(writer, "Total tests: %,d, total failures: %,d (rate %.2f%%), speed: %.2f tests/sec, total time: %s, GC runs: %,d, GC time: %s%n",
                count, errors, 100d * errors / count, totalRate, formatTime((long) (elapsed / 1_000_000_000d)), gcRuns, formatTime(gcTime / 1000));
    }

    @Override
    public void printStatistics(PrintWriter writer) {
        int errors = errorCount.get();
        if (errors == 0) {
            return;
        }

        writer.println();
        writer.println("Failure distribution per goal / optimization settings:");
        writer.println();
        writer.printf("%-35s", "Generation goal");
        for (GenerationGoal goal : GenerationGoal.values()) {
            writer.printf("%15s", goal.name().charAt(0) + goal.name().substring(1).toLowerCase());
        }
        writer.println();

        writer.printf("%-35s", "");
        for (GenerationGoal level : GenerationGoal.values()) {
            if (configuration.getGenerationGoals().contains(level)) {
                writer.printf("%14.1f%%", 100d * goalStatistics.get(level).get() / errors);
            } else {
                writer.printf("%15s", "-");
            }
        }
        writer.println();

        writer.println();
        writer.printf("%-35s", "Optimization level");
        for (OptimizationLevel level : OptimizationLevel.values()) {
            writer.printf("%15s", level.name().charAt(0) + level.name().substring(1).toLowerCase());
        }
        writer.println();

        for (Optimization optimization : Optimization.values()) {
            List<OptimizationLevel> validLevels = configuration.getOptimizationLevels().get(optimization);
            writer.printf("%-35s", optimization.getName());
            for (OptimizationLevel level : OptimizationLevel.values()) {
                if (validLevels.contains(level)) {
                    writer.printf("%14.1f%%", 100d * statistics.get(optimization).get(level).get() / errors);
                } else {
                    writer.printf("%15s", "-");
                }
            }
            writer.println();
        }
    }

    private static void printf(PrintWriter writer, @PrintFormat String format, Object... args) {
        System.out.printf(format, args);
        writer.printf(format, args);
    }

    private static String formatTime(long time) {
        if (time < 0) {
            return "-:--:--";
        } else {
            long hours = time / 3600;
            long minutes = time % 3600 / 60;
            long seconds = time % 60;
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
    }
}
