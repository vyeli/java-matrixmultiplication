package itba.paralelo.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorMultiplier implements MatrixMultiplier {
    private final int threads;
    
    public ExecutorMultiplier(int threads) {
        this.threads = threads;
    }
    
    @Override
    public double[][] multiply(double[][] A, double[][] B) {
        int size = A.length;
        double[][] C = new double[size][size];
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();
        
        int rowsPerThread = size / threads;
        
        for (int t = 0; t < threads; t++) {
            final int startRow = t * rowsPerThread;
            final int endRow = (t == threads - 1) ? size : startRow + rowsPerThread;
            
            futures.add(executor.submit(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < size; j++) {
                        for (int k = 0; k < size; k++) {
                            C[i][j] += A[i][k] * B[k][j];
                        }
                    }
                }
            }));
        }
        
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        executor.shutdown();
        return C;
    }

    @Override
    public String getName() {
        return "executor";
    }

    @Override
    public int getThreadCount() {
        return threads;
    }
}