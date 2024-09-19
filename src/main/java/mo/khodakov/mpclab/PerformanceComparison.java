package mo.khodakov.mpclab;

public class PerformanceComparison {

    public static final String MOVES = " moves";

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3) {
            System.err.println("Please provide N (number of cells), K (number of particles), and p (probability threshold)");
            System.exit(1);
        }

        System.out.println("Starting performance comparison...");

        // Запуск і вимірювання продуктивності Cells0
        System.out.println("\nRunning Cells0 (without synchronization)...");
        long cells0Moves = measurePerformance(() -> {
            try {
                Cells0.main(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Запуск і вимірювання продуктивності Cells1
        System.out.println("\nRunning Cells1 (synchronized)...");
        long cells1Moves = measurePerformance(() -> {
            try {
                Cells1.main(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Запуск і вимірювання продуктивності Cells2
        System.out.println("\nRunning Cells2 (ReentrantLock for the entire array)...");
        long cells2Moves = measurePerformance(() -> {
            try {
                Cells2.main(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Виведення результатів
        System.out.println("\nPerformance results (number of moves):");
        System.out.println("Cells0 (without synchronization): " + cells0Moves + MOVES);
        System.out.println("Cells1 (synchronized): " + cells1Moves + MOVES);
        System.out.println("Cells2 (ReentrantLock for the entire array): " + cells2Moves + MOVES);
    }

    /**
     * Вимірює кількість виконаних переміщень частинок за даний час.
     */
    private static long measurePerformance(Runnable task) throws InterruptedException {
        // Змінна для зберігання кількості переміщень
        PerformanceCounter.reset();
        Thread thread = new Thread(task);
        thread.start();
        thread.join(); // Очікуємо завершення виконання задачі
        return PerformanceCounter.getMoves();
    }
}
