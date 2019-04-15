package command;

import client.RMQClient;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import configuration.RabbitMQConfiguration;
import di.DI;
import resource.RequestType;

public class RMQCommand {

    private static RMQClient rmqClient = null;
    private boolean transactional;
    private boolean durable;
    private RequestType requestType;
    private static final String queueName = "load-test";


    public RMQCommand(String string, boolean transactional, boolean durable, RequestType requestType) {
        RabbitMQConfiguration rabbitMQConfiguration = new RabbitMQConfiguration("localhost");
        if (rmqClient == null) {
            rmqClient = new RMQClient(rabbitMQConfiguration, DI.di().getInstance(MetricRegistry.class));
        }
        this.transactional = transactional;
        this.durable = durable;
        this.requestType = requestType;
    }

    @Timed
    public String run() throws Exception {
        if (RequestType.WRITE.equals(requestType)) {
            if (!transactional) {
                rmqClient.publish("entity", queueName, durable);
            } else {
                rmqClient.publishTransaction("entity", queueName , durable);
            }
        } else {
            rmqClient.read(queueName);
        }

        return null;
    }
}
