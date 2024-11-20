package itba.paralelo.utils;

import java.time.Duration;
import java.time.Instant;

public class TestRunner {
    private static final int WARMUP_ITERATIONS = 2;
    private static final int TEST_ITERATIONS = 5;

    public static double[] runTest(String implementation, int threads, Runnable test) {
        double[] times = new double[TEST_ITERATIONS];
        
        // Warmup
        System.out.printf("\nRunning %s with %d threads\n", implementation, threads);
        System.out.println("Warming up...");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            test.run();
        }

        // Real tests
        System.gc();
        System.out.println("Running tests...");
        
        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Instant start = Instant.now();
            test.run();
            Instant end = Instant.now();
            
            double time = Duration.between(start, end).toNanos() / 1_000_000_000.0;
            times[i] = time;
            
            System.out.printf("  Iteration %d: %.3f seconds\n", i + 1, time);
        }

        // Calcular y mostrar estadísticas
        double mean = calculateMean(times);
        double stdDev = calculateStdDev(times, mean);
        System.out.printf("  Mean: %.3f seconds, StdDev: %.3f seconds\n", mean, stdDev);

        return times;
    }

    private static double calculateMean(double[] times) {
        double sum = 0;
        for (double time : times) {
            sum += time;
        }
        return sum / times.length;
    }

    private static double calculateStdDev(double[] times, double mean) {
        double sumSquareDiff = 0;
        for (double time : times) {
            sumSquareDiff += Math.pow(time - mean, 2);
        }
        return Math.sqrt(sumSquareDiff / times.length);
    }
}