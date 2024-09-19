package mo.khodakov.mpclab;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Версія без синхронізації
 */
public class Cells0 {

    // клітинки
    private int n;

    // кількість атомів
    private int k;

    // поріг ймовірності для подальшого руху частинки
    private double p;

    private static final int TIME_UNIT_MS = 100;

    // cells[0] - кількісь атомів в i-тій клітінці
    int[] cells;

    public static void main(String[] args) throws InterruptedException {
        Cells0 cells = new Cells0(args);
        // створюємо пул потоків
        ExecutorService executorService = Executors.newFixedThreadPool(cells.k);

        System.out.println("Starting modelling. Number of threads: " + cells.k);

        // запускаємо роботу частинок
        for (int i = 0; i < cells.k; i++) {
            executorService.execute(cells.new Particle());
        }

        System.out.println("Waiting for 10 seconds...");
        TimeUnit.SECONDS.sleep(10);
        executorService.shutdownNow();

        System.out.println("Threads have finished executions.");
        int newK = 0;
        for (int i = 0; i < cells.n; i++) {
            newK += cells.cells[i];
        }
        System.out.println("Old atoms amount: " + cells.k);
        System.out.println("New atoms amount: " + newK);
    }

    public Cells0(String[] args) {
        try {
            this.n = Integer.parseInt(args[0]);
            this.k = Integer.parseInt(args[1]);
            this.p = Double.parseDouble(args[2]);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Please check that N, K, p are correctly");
            System.exit(1);
        }
        if (n <= 0 || k <= 0 || p > 0.5 || p <= 0) {
            System.err.println("Incorrect parameters");
            System.exit(1);
        }
        this.cells = new int[n];
        this.cells[0] = k;
    }

    public void moveParticle(int from, int to) {
        cells[from]--;
        cells[to]++;
        PerformanceCounter.increment(); // Додаємо інкремент до лічильника переміщень
        StringBuilder sb = new StringBuilder();
        for (int c : cells) {
            sb.append(c).append(" ");
        }
        System.out.println(sb);
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
