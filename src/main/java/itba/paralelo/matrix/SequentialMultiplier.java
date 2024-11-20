package itba.paralelo.matrix;

public class SequentialMultiplier implements MatrixMultiplier {
    
    @Override
    public double[][] multiply(double[][] A, double[][] B) {
        int size = A.length;
        double[][] C = new double[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return C;
    }

    @Override
    public String getName() {
        return "sequential";
    }

    @Override
    public int getThreadCount() {
        return 1;
    }
}