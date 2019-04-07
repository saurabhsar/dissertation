package load.gen;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import di.DI;

import java.util.concurrent.atomic.AtomicLong;

public class ThreadedLoadGenerator extends Thread {
    private double load;
    private long duration;
    private MetricRegistry metricRegistry;
    private Counter counter;

    public ThreadedLoadGenerator(String name, double load, long duration) {
        super(name);
        this.load = load;
        this.duration = duration;
        this.metricRegistry = DI.di().getInstance(MetricRegistry.class);
        counter = metricRegistry.counter("threadCounter");
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
                counter.inc();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
