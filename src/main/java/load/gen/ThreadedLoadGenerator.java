package load.gen;

public class ThreadedLoadGenerator extends Thread {
    private double load;
    private long duration;

    public ThreadedLoadGenerator(String name, double load, long duration) {
        super(name);
        this.load = load;
        this.duration = duration;
    }

    public void run(LoadGenI loadGenI) {
        long startTime = System.currentTimeMillis();
        try {
            // Loop for the given duration
            while (System.currentTimeMillis() - startTime < duration) {
                // Every 100ms, sleep for the percentage of unladen time
                if (System.currentTimeMillis() % 100 == 0) {
                    Thread.sleep((long) Math.floor((1 - load) * 100));
                }
                loadGenI.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
