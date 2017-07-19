package solutions.alterego.androidbound;

public class TestUtils {

    private static long tickTime;

    public static void tick() {
        tickTime = System.nanoTime();
    }

    public static void tock(String action) {
        long mstime = (System.nanoTime() - tickTime) / 1000000;
        System.out.println(action + ": " + mstime + "ms");
    }

}
