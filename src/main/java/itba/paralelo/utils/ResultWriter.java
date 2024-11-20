package itba.paralelo.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class ResultWriter {
    private static final String RESULTS_DIR = "results";

    public static void init() {
        new File(RESULTS_DIR).mkdirs();
        try (PrintWriter writer = new PrintWriter(RESULTS_DIR + "/times.txt")) {
            writer.println("implementation,threads,iteration,time");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeSystemInfo(int size) {
        try (PrintWriter writer = new PrintWriter(RESULTS_DIR + "/system_info.txt")) {
            writer.println("System Information");
            writer.println("----------------");
            writer.printf("Test Date: %s%n", LocalDateTime.now());
            writer.printf("Matrix Size: %dx%d%n", size, size);
            writer.printf("Available Processors: %d%n", 
                Runtime.getRuntime().availableProcessors());
            writer.printf("Java Version: %s%n", System.getProperty("java.version"));
            writer.printf("OS: %s %s%n", System.getProperty("os.name"), 
                System.getProperty("os.version"));
            writer.printf("Max Memory: %d MB%n", 
                Runtime.getRuntime().maxMemory() / (1024*1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeResults(String implementation, int threads, 
                                  double[] times) {
        try (PrintWriter writer = 
                new PrintWriter(new FileWriter(RESULTS_DIR + "/times.txt", true))) {
            
            double sum = 0;
            for (int i = 0; i < times.length; i++) {
                writer.printf("%s,%d,%d,%.3f\n", 
                    implementation, threads, i + 1, times[i]);
                sum += times[i];
            }
            
            double mean = sum / times.length;
            double stdDev = calculateStdDev(times, mean);

            writer.printf("# %s with %d threads - Mean: %.3f s, StdDev: %.3f s\n", 
                implementation, threads, mean, stdDev);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateStdDev(double[] times, double mean) {
        double sumSquareDiff = 0;
        for (double time : times) {
            sumSquareDiff += Math.pow(time - mean, 2);
        }
        return Math.sqrt(sumSquareDiff / times.length);
    }
}