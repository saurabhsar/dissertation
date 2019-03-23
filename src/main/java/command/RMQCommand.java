package command;

import client.RMQClient;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import configuration.RabbitMQConfiguration;

public class RMQCommand extends HystrixCommand<String> {

    private static RMQClient rmqClient = null;
    private boolean transactional;

    public RMQCommand(String string, boolean transactional) {
        super(HystrixCommandGroupKey.Factory.asKey(string));
        RabbitMQConfiguration rabbitMQConfiguration = new RabbitMQConfiguration("localhost");
        if (rmqClient == null) {
            rmqClient = new RMQClient(rabbitMQConfiguration, new MetricRegistry());
        }
        this.transactional = transactional;
    }

    @Override
    protected String run() throws Exception {
        if (!transactional) {
            rmqClient.publish("entity", "load-test");
        } else {
            rmqClient.publishTransaction("entity", "load-test");
        }

        return null;
    }
}
