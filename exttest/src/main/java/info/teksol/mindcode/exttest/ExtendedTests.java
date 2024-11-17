package info.teksol.mindcode.exttest;

import info.teksol.mindcode.compiler.optimization.Optimization;
import info.teksol.mindcode.compiler.optimization.OptimizationCoordinator;
import info.teksol.mindcode.exttest.cases.TestCaseExecutor;
import info.teksol.mindcode.exttest.cases.TestCaseSelector;
import info.teksol.mindcode.exttest.cases.TestCaseSelectorSampled;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ExtendedTests {
    private static final Set<Optimization> IGNORED = EnumSet.of(
            Optimization.CASE_EXPRESSION_OPTIMIZATION,
            Optimization.CASE_SWITCHING,
            Optimization.STACK_OPTIMIZATION
    );

    public static final List<Optimization> LIST = Optimization.LIST.stream()
            .filter(o -> !IGNORED.contains(o))
            .toList();

    public static void main(String[] args) throws InterruptedException {
        int allTests = 1 << LIST.size();
        int samples = 10000;

        TestCaseExecutor testCaseExecutor = new TestCaseExecutor();
        //TestCaseSelector testCaseSelector = new TestCaseSelectorFull();
        TestCaseSelector testCaseSelector = new TestCaseSelectorSampled(allTests, samples);


        if (OptimizationCoordinator.isDebugOn()) {
            System.out.println("Error: debug active");
            return;
        }

        File file = new File("results.txt");
        ForkJoinPool forkJoinPool = new ForkJoinPool(24);

        long start = System.nanoTime();
        ForkJoinTask<Integer> task = forkJoinPool.submit(new TestRunner(testCaseSelector, testCaseExecutor, 0, samples));

        try (PrintWriter writer = new PrintWriter(file)) {
            long lastTime = System.nanoTime();
            int lastCount = 0;
            long lastGcTime = 0;
            long lastGcRuns = 0;
            System.out.println("Warming up...");
            Thread.sleep(10_000);
            while (true) {
                try {
                    long time = System.nanoTime();
                    long elapsed = time - start;
                    int count = testCaseExecutor.testCount.get();
                    int errors = testCaseExecutor.errorCount.get();
                    double rate = (count - lastCount) * 1_000_000_000d / (time - lastTime);
                    double totalRate = count * 1_000_000_000d / elapsed;
                    int remaining = (int) ((allTests - count) / totalRate);

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
                                      "    memory size %,5d MB, GC: %+,d runs, %+.3f s%n" ,
                            count, samples, 100d * count / samples, errors, 100d * errors / samples, rate,
                            formatTime((int) (elapsed / 1_000_000_000d)), formatTime(remaining),
                            (int) used, (gcRuns - lastGcRuns), (gcTime - lastGcTime) / 1000.0);

                    lastGcRuns = gcRuns;
                    lastGcTime = gcTime;
                    lastCount = count;
                    lastTime = time;

                    ErrorResult errorResult;
                    while ((errorResult = testCaseExecutor.errors.poll()) != null) {
                        writer.println(errorResult);
                    }
                    writer.flush();

                    task.get(5, TimeUnit.SECONDS);
                    break;
                } catch (TimeoutException ignored) {
                }
            }

            long elapsed = System.nanoTime() - start;
            int count = testCaseExecutor.testCount.get();
            int errors = testCaseExecutor.errorCount.get();
            double totalRate = count * 1_000_000_000d / elapsed;

            List<GarbageCollectorMXBean> gcCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
            long gcRuns = 0;
            long gcTime = 0;
            for (GarbageCollectorMXBean gcBean : gcCollectorMXBeans) {
                gcRuns += gcBean.getCollectionCount();
                gcTime += gcBean.getCollectionTime();
            }

            System.out.println();
            System.out.println("Finished.");
            System.out.printf("Total tests: %,d, total failures: %,d (rate %.2f%%), speed: %.2f tests/sec, total time: %s, GC runs: %,d, GC time: %s%n",
                    count, errors, 100d * errors / count, totalRate, formatTime((long)(elapsed / 1_000_000_000d)), gcRuns, formatTime(gcTime / 1000));

            writer.println();
            writer.println("Finished.");
            writer.printf("Total tests: %,d, total failures: %,d (rate %.2f%%), speed: %.2f tests/sec, total time: %s, GC runs: %,d, GC time: %s%n",
                    count, errors, 100d * errors / count, totalRate, formatTime((long)(elapsed / 1_000_000_000d)), gcRuns, formatTime(gcTime / 1000));
        } catch (ExecutionException e) {
            System.out.println("Error executing tests file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error writing results to file");
            e.printStackTrace();
        } finally {
            task.cancel(true);
        }
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