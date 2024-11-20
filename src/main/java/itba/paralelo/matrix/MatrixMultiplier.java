package itba.paralelo.matrix;

public interface MatrixMultiplier {
    double[][] multiply(double[][] A, double[][] B);
    String getName();
    int getThreadCount();
}
