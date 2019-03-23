package client;

import configuration.RabbitMQConfiguration;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import lombok.extern.slf4j.Slf4j;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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

    public RMQClient(RabbitMQConfiguration rabbitMQConfiguration, MetricRegistry metricRegistry) {
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.metricRegistry = metricRegistry;
        if (factory == null) {
            factory = new ConnectionFactory();
        }
        if (connection == null) {
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

        initializeMetrics();
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

    public void publish(String entity, String queueName) throws TimeoutException {

        factory.setHost(rabbitMQConfiguration.getHostName());
        Timer.Context timerContext = rmqRefPushTimer.time();
        try {

            try {
                nonTransactionChannel.basicPublish("", queueName, null, entity.getBytes());

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

    public void publishTransaction(String entity, String queueName) throws TimeoutException {

        Timer.Context timerContext = rmqTransactionalTimer.time();

        try {
            try {
                transactionalChannel.txSelect();
                transactionalChannel.basicPublish("", queueName, null, entity.getBytes());

                log.info("offerRefIds has been published. Going to commit the transaction");
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
}
