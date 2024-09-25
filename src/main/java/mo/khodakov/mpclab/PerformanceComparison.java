package mo.khodakov.mpclab;

public class PerformanceComparison {

    public static final String MOVES = " moves";
    public static final String TIME = ", Time: ";

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3) {
            System.err.println("Please provide N (number of cells), K (number of particles), and p (probability threshold)");
            System.exit(1);
        }

        System.out.println("Starting performance comparison...");

        // Запуск і вимірювання продуктивності Cells0
        System.out.println("\nRunning Cells0 (without synchronization)...");
        long cells0Moves;
        long cells0Time;
        cells0Time = measurePerformance(() -> {
            try {
                Cells0.main(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        cells0Moves = PerformanceCounter.getMoves(); // Отримуємо кількість переміщень для Cells0

        // Запуск і вимірювання продуктивності Cells1
        System.out.println("\nRunning Cells1 (synchronized object level)...");
        long cells1Moves;
        long cells1Time;
        cells1Time = measurePerformance(() -> {
            try {
                Cells1.main(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        cells1Moves = PerformanceCounter.getMoves(); // Отримуємо кількість переміщень для Cells1

        // Запуск і вимірювання продуктивності Cells2
        System.out.println("\nRunning Cells2 (cell-level synchronization)...");
        long cells2Moves;
        long cells2Time;
        cells2Time = measurePerformance(() -> {
            try {
                Cells2.main(args);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        cells2Moves = PerformanceCounter.getMoves(); // Отримуємо кількість переміщень для Cells2

        // Виведення результатів
        System.out.println("\nPerformance results (number of moves and time taken):");
        System.out.println("Cells0 (without synchronization): " + cells0Moves + MOVES + TIME + cells0Time + " ms");
        System.out.println("Cells1 (synchronized): " + cells1Moves + MOVES + TIME + cells1Time + " ms");
        System.out.println("Cells2 (cell-level synchronization): " + cells2Moves + MOVES + TIME + cells2Time + " ms");
    }

    /**
     * Вимірює кількість часу виконання задачі та повертає час у мілісекундах.
     */
    private static long measurePerformance(Runnable task) throws InterruptedException {
        PerformanceCounter.reset(); // Скидаємо лічильник переміщень

        long startTime = System.currentTimeMillis(); // Початок вимірювання часу
        Thread thread = new Thread(task);
        thread.start();
        thread.join(); // Очікуємо завершення виконання задачі
        long endTime = System.currentTimeMillis(); // Кінець вимірювання часу

        return endTime - startTime; // Час виконання в мілісекундах
    }
}
