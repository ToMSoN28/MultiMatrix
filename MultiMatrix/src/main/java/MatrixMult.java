import java.io.*;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMult {

    public static void main(String[] args) {
        MatrixMult mm = new MatrixMult();

        try {
            mm.start(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void start(String[] args) throws FileNotFoundException {
        Matrix A,B;
        A = read(args[0]);
        B = read(args[1]);
        int num_threads = Integer.parseInt(args[2]);

        System.out.println("Wczytalem A:");
        print(A);

        System.out.println("\nWczytalem B:");
        print(B);

        long startTime = System.nanoTime();
        Matrix C = mult(A,B, num_threads);
        long endTime = System.nanoTime();
        System.out.println("A*B = ");

        print(C);

        long startTime1 = System.nanoTime();
        Matrix szmurlor = new Matrix(A.rows(), B.cols());
        for (int r = 0; r < A.rows(); r++) {
            for (int c = 0; c < B.cols(); c++) {
                float s = 0;
                for (int k = 0; k < A.cols(); k++) {
                    s += A.get(r,k) * B.get(k, c);
                }
                szmurlor.set(r,c,s);
            }
        }
        long endTime1 = System.nanoTime();
        print(szmurlor);

        float sum = 0;
        for (int r = 0; r < C.rows(); r++) {
            for (int c = 0; c < C.cols(); c++) {
                sum += C.get(r, c);
                if (C.get(r, c) != szmurlor.get(r, c)) {
                    System.out.println("not equal");
                }
            }
        }

        System.out.println("Sum of elements: " + sum);
        System.out.println("Time using threat: "+(endTime-startTime));
        System.out.println("Time without threat: "+(endTime1-startTime1));

    }

    private Matrix mult(Matrix A, Matrix B, int num_threads) {
        Matrix C = new Matrix(A.rows(), B.cols());

        ExecutorService executor = Executors.newFixedThreadPool(num_threads);

        int chunkSize = A.rows() / num_threads;

        Thread[] threads = new Thread[num_threads];

        for (int i = 0; i < num_threads; i++) {
            int startRow = i * chunkSize;
            int endRow = (i == num_threads - 1) ? A.rows() : (i + 1) * chunkSize;
            MatrixMultiplier task = new MatrixMultiplier(A, B, C, startRow, endRow);
            executor.execute(task);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return C;
    }

    protected Matrix read(String fname) throws FileNotFoundException {
        File f = new File(fname);
        Scanner scanner = new Scanner(f).useLocale(Locale.ENGLISH);

        int rows  = scanner.nextInt();
        int cols  = scanner.nextInt();
        System.out.println(rows+"    "+cols);
        Matrix res = new Matrix(rows,cols);

        for (int r = 0; r < res.rows(); r++) {
            for (int c = 0; c < res.cols(); c++) {
                res.set(r, c, scanner.nextFloat());
            }
        }
        return res;
    }

    protected void print(Matrix m) {
        System.out.println("[");
        for (int r = 0; r < m.rows(); r++) {

            for (int c = 0; c < m.cols(); c++) {
                System.out.print(m.get(r,c));
                System.out.print(" ");
            }

            System.out.println("");
        }
        System.out.println("]");
    }


    public class Matrix {
        private int ncols;
        private int nrows;
        private float _data[];

        public Matrix(int r, int c) {
            this.ncols = c;
            this.nrows = r;
            _data = new float[c*r];
        }

        public float get(int r, int c) {
            return _data[r*ncols + c];
        }

        public void set(int r, int c, float v) {
            _data[r*ncols +c] = v;
        }

        public int rows() {
            return nrows;
        }

        public int cols() {
            return ncols;
        }
    }

    class MatrixMultiplier implements Runnable {
        private final Matrix A;
        private final Matrix B;
        private final Matrix C;
        private final int startRow;
        private final int endRow;

        public MatrixMultiplier(Matrix A, Matrix B, Matrix C, int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int r = startRow; r < endRow; r++) {
                for (int c = 0; c < B.cols(); c++) {
                    float s = 0;
                    for (int k = 0; k < A.cols(); k++) {
                        s += A.get(r, k) * B.get(k, c);
                    }
                    C.set(r, c, s);
                }
            }
        }
    }


}
