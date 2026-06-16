package info.teksol.mc.async;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@NullMarked
public class AsyncExecutor {
    private static @Nullable ExecutorService executor;

    public static void start(int processors) {
        int parallelism = processors <= 0
                ? Runtime.getRuntime().availableProcessors()
                : Math.min(processors, Runtime.getRuntime().availableProcessors());

        if (parallelism != 1) {
            executor = Executors.newWorkStealingPool(parallelism);
        }
    }

    public static void stop() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    public static <T> CompletableFuture<T> execute(Supplier<T> supplier) {
        return executor == null
                ? CompletableFuture.completedFuture(supplier.get())
                : CompletableFuture.supplyAsync(supplier, executor);
    }
}
