import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FastParallelCalculations implements ParallelCalculations {
    @Override
    public List<List<Double>> map(Function function, int size, int threads) {
        // Initialize the result matrix
        List<List<Double>> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                row.add(0.0);
            }
            result.add(row);
        }

        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        // Calculate how many rows each thread should process
        int rowsPerThread = size / threads;
        int extraRows = size % threads;

        // Create and submit tasks
        int startRow = 0;
        for (int i = 0; i < threads; i++) {
            int rowsForThisThread = rowsPerThread + (i < extraRows ? 1 : 0);
            int endRow = startRow + rowsForThisThread;

            // Create task for current thread
            final int threadStartRow = startRow;
            final int threadEndRow = endRow;

            futures.add(executor.submit(() -> {
                for (int row = threadStartRow; row < threadEndRow; row++) {
                    for (int col = 0; col < size; col++) {
                        double value = function.get(row, col);
                        result.get(row).set(col, value);
                    }
                }
                return null;
            }));

            startRow = endRow;
        }

        // Wait for all tasks to complete
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException("Calculation failed", e);
            }
        }

        // Shutdown executor service
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return result;
    }
}
