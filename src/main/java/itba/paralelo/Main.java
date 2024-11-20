package itba.paralelo;

import java.time.Duration;
import java.time.Instant;

import itba.paralelo.matrix.ExecutorMultiplier;
import itba.paralelo.matrix.ForkJoinMultiplier;
import itba.paralelo.matrix.MatrixGenerator;
import itba.paralelo.matrix.SequentialMultiplier;
import itba.paralelo.utils.ResultWriter;
import itba.paralelo.utils.TestRunner;

public class Main {
    private static final int SIZE = 1024;
    private static final int[] THREAD_COUNTS = {2, 4, 6, 8, 10, 12};
    
    public static void main(String[] args) {
        Instant startProgram = Instant.now();
        
        System.out.println("=== Matrix Multiplication Test ===");
        System.out.println("Size: " + SIZE + "x" + SIZE);
        System.out.println("Cores available: " + Runtime.getRuntime().availableProcessors());
        
        // Initialize matrices
        Instant startInit = Instant.now();
        double[][] A = MatrixGenerator.generate(SIZE);
        double[][] B = MatrixGenerator.generate(SIZE);
        double initTime = Duration.between(startInit, Instant.now()).toMillis() / 1000.0;
        System.out.printf("Initialization time: %.3f seconds\n", initTime);
        
        // Setup results
        ResultWriter.init();
        ResultWriter.writeSystemInfo(SIZE);
        
        // Sequential test
        double[] seqTimes = TestRunner.runTest(
            "sequential", 
            1, 
            () -> new SequentialMultiplier().multiply(A, B)
        );
        ResultWriter.writeResults("sequential", 1, seqTimes);
        
        // Parallel tests
        for (int threads : THREAD_COUNTS) {
            // ExecutorService
            double[] execTimes = TestRunner.runTest(
                "executor",
                threads,
                () -> new ExecutorMultiplier(threads).multiply(A, B)
            );
            ResultWriter.writeResults("executor", threads, execTimes);
            
            // ForkJoin
            double[] fjTimes = TestRunner.runTest(
                "forkjoin",
                threads,
                () -> new ForkJoinMultiplier(threads).multiply(A, B)
            );
            ResultWriter.writeResults("forkjoin", threads, fjTimes);
        }
        
        double totalTime = Duration.between(startProgram, Instant.now()).toMillis() / 1000.0;
        System.out.printf("\nTotal execution time: %.3f seconds\n", totalTime);
    }
}