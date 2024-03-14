import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Random;

public class MatrixGenerator {

    public static void generateMatrixToFile(int rows, int cols) {
        double[][] matrix = generateMatrix(rows, cols);

        try {
            PrintWriter writer = new PrintWriter(new FileWriter("gen.txt"));

            writer.println(cols);
            writer.println(rows);

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.printf(Locale.US, "%.6f", matrix[i][j]);
                    if (j < cols - 1) {
                        writer.print("   ");
                    }
                }
                writer.println();
            }

            writer.close();
            System.out.println("Matrix generated and saved to gen.txt successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static double[][] generateMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextDouble();
            }
        }

        return matrix;
    }

    public static void main(String[] args) {
        int rows = 8;
        int cols = 16;
        generateMatrixToFile(rows, cols);
    }
}