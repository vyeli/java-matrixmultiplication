package itba.paralelo.matrix;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinMultiplier implements MatrixMultiplier {
    private final int threads;
    private static final int THRESHOLD = 64;
    
    public ForkJoinMultiplier(int threads) {
        this.threads = threads;
    }
    
    private static class MultiplyTask extends RecursiveAction {
        private final double[][] A, B, C;
        private final int startRow, endRow;
        
        MultiplyTask(double[][] A, double[][] B, double[][] C, 
                    int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected void compute() {
            if (endRow - startRow <= THRESHOLD) {
                computeDirectly();
            } else {
                int middle = (startRow + endRow) / 2;
                invokeAll(
                    new MultiplyTask(A, B, C, startRow, middle),
                    new MultiplyTask(A, B, C, middle, endRow)
                );
            }
        }

        private void computeDirectly() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < C.length; j++) {
                    for (int k = 0; k < A.length; k++) {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        }
    }
    
    @Override
    public double[][] multiply(double[][] A, double[][] B) {
        double[][] C = new double[A.length][A.length];
        ForkJoinPool pool = new ForkJoinPool(threads);
        pool.invoke(new MultiplyTask(A, B, C, 0, A.length));
        pool.shutdown();
        return C;
    }

    @Override
    public String getName() {
        return "forkjoin";
    }

    @Override
    public int getThreadCount() {
        return threads;
    }
}
