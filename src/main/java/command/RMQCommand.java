package command;

import client.RMQClient;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import configuration.RabbitMQConfiguration;
import di.DI;

public class RMQCommand extends HystrixCommand<String> {

    private static RMQClient rmqClient = null;
    private boolean transactional;

    public RMQCommand(String string, boolean transactional) {
        super(HystrixCommandGroupKey.Factory.asKey(string));
        RabbitMQConfiguration rabbitMQConfiguration = new RabbitMQConfiguration("localhost");
        if (rmqClient == null) {
            rmqClient = new RMQClient(rabbitMQConfiguration, DI.di().getInstance(MetricRegistry.class));
        }
        this.transactional = transactional;
    }

    @Override
    @Timed
    protected String run() throws Exception {
        if (!transactional) {
            rmqClient.publish("entity", "load-test");
        } else {
            rmqClient.publishTransaction("entity", "load-test");
        }

        return null;
    }
}
