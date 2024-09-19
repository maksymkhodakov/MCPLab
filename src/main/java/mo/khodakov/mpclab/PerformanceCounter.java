package mo.khodakov.mpclab;


public class PerformanceCounter {

    private PerformanceCounter() {
    }

    private static long moveCounter = 0;

    public static synchronized void increment() {
        moveCounter++;
    }

    public static synchronized long getMoves() {
        return moveCounter;
    }

    public static synchronized void reset() {
        moveCounter = 0;
    }
}
