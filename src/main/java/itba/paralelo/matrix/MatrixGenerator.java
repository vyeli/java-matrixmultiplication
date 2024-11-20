package itba.paralelo.matrix;

import java.util.Random;

public class MatrixGenerator {
    private static final long SEED = 6834723L;
    
    public static double[][] generate(int size) {
        Random rand = new Random(SEED);
        double[][] matrix = new double[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rand.nextDouble();
            }
        }
        return matrix;
    }
}