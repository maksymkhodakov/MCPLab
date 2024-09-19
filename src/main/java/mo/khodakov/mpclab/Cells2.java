package mo.khodakov.mpclab;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Версія з блокуванням всього масиву клітинок
 */
public class Cells2 {

    // клітинки
    private int n;

    // кількість атомів
    private int k;

    // поріг ймовірності для подальшого руху частинки
    private double p;

    private static final int TIME_UNIT_MS = 100;

    // cells[0] - кількісь атомів в i-тій клітінці
    int[] cells;

    // mutual exclusion для масіву cells
    private final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Cells2 cells = new Cells2(args);
        // створюємо пул потоків
        ExecutorService executorService = Executors.newFixedThreadPool(cells.k);

        System.out.println("Starting modelling. Number of threads: " + cells.k);

        // запускаємо роботу частинок
        for (int i = 0; i < cells.k; i++) {
            executorService.execute(cells.new Particle());
        }

        System.out.println("Waiting for 15 seconds...");
        TimeUnit.SECONDS.sleep(15);
        executorService.shutdownNow();

        System.out.println("Threads have finished executions.");
        int newK = 0;
        for (int i = 0; i < cells.n; i++) {
            newK += cells.getCell(i); // Using getCell() to access array safely
        }
        System.out.println("Old atoms amount: " + cells.k);
        System.out.println("New atoms amount: " + newK);
    }

    public Cells2(String[] args) {
        try {
            this.n = Integer.parseInt(args[0]);
            this.k = Integer.parseInt(args[1]);
            this.p = Double.parseDouble(args[2]);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Please check that N, K, p are correct");
            System.exit(1);
        }
        if (n <= 0 || k <= 0 || p > 0.5 || p <= 0) {
            System.err.println("Incorrect parameters");
            System.exit(1);
        }
        this.cells = new int[n];
        this.cells[0] = k;
    }

    // синхронізований доступ до взяття значення
    public int getCell(int index) {
        lock.lock();
        try {
            return cells[index];
        } finally {
            lock.unlock();
        }
    }

    // синхронізований доступ до задання значення клітинки
    public void setCell(int index, int value) {
        lock.lock();
        try {
            cells[index] = value;
        } finally {
            lock.unlock();
        }
    }

    public void moveParticle(int from, int to) {
        lock.lock();
        try {
            setCell(from, getCell(from) - 1);
            setCell(to, getCell(to) + 1);
            StringBuilder sb = new StringBuilder();
            for (int c : cells) {
                sb.append(c).append(" ");
            }
            System.out.println(sb);
        } finally {
            lock.unlock();
        }
    }

    private class Particle implements Runnable {

        private final Random random = new Random();

        private int cell = 0;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                double probability = random.nextDouble();
                // go left
                if (probability <= p && cell != 0) {
                    moveParticle(cell, cell - 1);
                    cell--;
                } else if (probability > p && cell != n - 1) { // go right
                    moveParticle(cell, cell + 1);
                    cell++;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(TIME_UNIT_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
