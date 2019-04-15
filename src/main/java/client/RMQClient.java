package client;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.rabbitmq.client.*;
import configuration.RabbitMQConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RMQClient {
    private RabbitMQConfiguration rabbitMQConfiguration;
    private MetricRegistry metricRegistry;
    private static Meter exceptionMeter;
    private static Timer rmqRefPushTimer;
    private static Timer rmqOPPushTimer;
    private static Timer rmqTransactionalTimer;
    private static ConnectionFactory factory = null;
    private static Connection connection = null;
    private static Channel nonTransactionChannel = null;
    private static Channel transactionalChannel = null;

    @Inject
    public RMQClient(RabbitMQConfiguration rabbitMQConfiguration, MetricRegistry metricRegistry) {
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.metricRegistry = metricRegistry;
        if (factory == null) {
            factory = new ConnectionFactory();
        }
        initializeChannels();
        initializeMetrics();
    }

    private void initializeChannels() {
        if (connection == null || nonTransactionChannel==null || transactionalChannel == null) {
            try {
                connection = factory.newConnection();
                nonTransactionChannel = connection.createChannel();
                transactionalChannel = connection.createChannel();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeMetrics() {
        if (exceptionMeter == null) {
            exceptionMeter = metricRegistry.meter("RMQExceptionMeter");
        }
        if (rmqRefPushTimer == null) {
            rmqRefPushTimer = metricRegistry.timer("RMQRefPushTimer");
        }
        if (rmqOPPushTimer == null) {
            rmqOPPushTimer = metricRegistry.timer("RMQOPPushTimer");
        }
        if (rmqTransactionalTimer == null) {
            rmqTransactionalTimer = metricRegistry.timer("RMQTransactionalTimer");
        }
    }

    public void publish(String entity, String queueName, boolean durable) throws TimeoutException {

        factory.setHost(rabbitMQConfiguration.getHostName());
        Timer.Context timerContext = rmqRefPushTimer.time();
        try {

            try {
                propertyBuildandPersist(entity, queueName, durable, nonTransactionChannel);

            } catch (IOException ioe) {
                exceptionMeter.mark();
                if (log.isErrorEnabled()) {
                    log.error("push to rmq failed", ioe);
                }
                throw new RuntimeException("Push to RMQ failed");
            }
        } finally {
            timerContext.stop();
        }
    }

    private static void propertyBuildandPersist(String entity, String queueName, boolean durable, Channel channel) throws IOException {
        AMQP.BasicProperties basicProperties = null;
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        if (durable) {
            basicProperties = builder.deliveryMode(2).build();
        }
        channel.basicPublish("", queueName, basicProperties, entity.getBytes());
    }

    public void publishTransaction(String entity, String queueName, boolean durable) throws TimeoutException {

        Timer.Context timerContext = rmqTransactionalTimer.time();

        try {
            try {
                transactionalChannel.txSelect();
                propertyBuildandPersist(entity, queueName, durable, transactionalChannel);

                log.info("Published. Going to commit the transaction");
                transactionalChannel.txCommit();
            } catch (Throwable throwable) {
                exceptionMeter.mark();
                if (transactionalChannel != null) {
                    transactionalChannel.txRollback();
                }
                if (log.isErrorEnabled()) {
                    log.error("push to rmq failed", throwable);
                }
                throw new RuntimeException("Push to RMQ failed");
            } finally {

            }
        } catch (IOException io) {
            log.error("channel close failed");
            throw new RuntimeException("Channel/Connection close failed");

        } finally {
            timerContext.stop();
        }
    }

    public void read(String queueName) {
        initializeChannels();
        boolean autoAck = false;
        GetResponse response = null;
        try {
            response = nonTransactionChannel.basicGet(queueName, autoAck);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
        } else {
            AMQP.BasicProperties props = response.getProps();
            long deliveryTag = response.getEnvelope().getDeliveryTag();

            try {
                nonTransactionChannel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
