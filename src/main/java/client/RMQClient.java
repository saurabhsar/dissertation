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

    public RMQClient(RabbitMQConfiguration rabbitMQConfiguration, MetricRegistry metricRegistry) {
        this.rabbitMQConfiguration = rabbitMQConfiguration;
        this.metricRegistry = metricRegistry;
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

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQConfiguration.getHostName());
        Connection connection = null;
        Channel channel = null;
        Timer.Context timerContext = rmqRefPushTimer.time();
        try {

            try {
                connection = factory.newConnection();
                channel = connection.createChannel();

                channel.basicPublish("", queueName, null, entity.getBytes());

            } catch (IOException ioe) {
                exceptionMeter.mark();
                if (log.isErrorEnabled()) {
                    log.error("push to rmq failed", ioe);
                }
                throw new RuntimeException("Push to RMQ failed");
            } finally {
                if (channel != null)
                    channel.close();
                if (connection != null)
                    connection.close();
            }
        } catch (IOException io) {
            log.error("channel close failed");
            throw new RuntimeException("Channel/Connection close failed");

        } finally {
            timerContext.stop();
        }
    }

    public void publishTransaction(String entity, String queueName) throws TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQConfiguration.getHostName());

        Timer.Context timerContext = rmqTransactionalTimer.time();
        Connection connection = null;

        try {
            Channel channel = null;
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();

                channel.txSelect();
                channel.basicPublish("", queueName, null, entity.getBytes());

                log.info("offerRefIds has been published. Going to commit the transaction");
                channel.txCommit();

            } catch (Throwable throwable) {
                exceptionMeter.mark();
                if (channel != null) {
                    channel.txRollback();
                }
                if (log.isErrorEnabled()) {
                    log.error("push to rmq failed", throwable);
                }
                throw new RuntimeException("Push to RMQ failed");
            } finally {
                if (channel != null)
                    channel.close();
                if (connection != null)
                    connection.close();
            }
        } catch (IOException io) {
            log.error("channel close failed");
            throw new RuntimeException("Channel/Connection close failed");

        } finally {
            timerContext.stop();
        }
    }
}
