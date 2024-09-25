package mo.khodakov.mpclab;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Cells2 {

    // клітинки
    private int n;

    // кількість атомів
    private int k;

    // поріг ймовірності для подальшого руху частинки
    private double p;

    private static final int TIME_UNIT_MS = 100;

    // cells[0] - кількість атомів в i-тій клітці
    int[] cells;

    // об'єкти для блокування клітинок
    private final Object[] cellLocks;

    public static void main(String[] args) throws InterruptedException {
        Cells2 cells = new Cells2(args);
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
        this.cellLocks = new Object[n]; // Кожна клітинка має свій об'єкт блокування
        for (int i = 0; i < n; i++) {
            cellLocks[i] = new Object(); // Ініціалізуємо кожен об'єкт
        }
        this.cells[0] = k; // Спочатку всі атоми в 0-й клітинці
    }

    // синхронізований доступ до взяття значення клітинки
    public int getCell(int index) {
        synchronized (cellLocks[index]) {
            return cells[index];
        }
    }

    // переміщення частинки між клітинками
    public void moveParticle(int from, int to) {
        // Спочатку блокуємо клітинки з меншою індексацією, щоб уникнути дедлоків
        if (from < to) {
            synchronized (cellLocks[from]) {
                synchronized (cellLocks[to]) {
                    performMove(from, to);
                }
            }
        } else {
            synchronized (cellLocks[to]) {
                synchronized (cellLocks[from]) {
                    performMove(from, to);
                }
            }
        }
    }

    // внутрішній метод для виконання переміщення
    private void performMove(int from, int to) {
        cells[from]--; // Зменшуємо кількість атомів у клітинці from
        cells[to]++;   // Збільшуємо кількість атомів у клітинці to
        PerformanceCounter.increment(); // Додаємо інкремент до лічильника переміщень
        StringBuilder sb = new StringBuilder();
        for (int c : cells) {
            sb.append(c).append(" ");
        }
        System.out.println(sb);
    }

    // Внутрішній клас, який представляє частинку
    private class Particle implements Runnable {

        private final Random random = new Random();
        private int cell = 0;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                double probability = random.nextDouble();
                // Йдемо вліво
                if (probability <= p && cell != 0) {
                    moveParticle(cell, cell - 1);
                    cell--;
                } else if (probability > p && cell != n - 1) { // Йдемо вправо
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
