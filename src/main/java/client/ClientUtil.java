package client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class ClientUtil {

    private static final int MAX_TOTAL_CONNECTIONS = 100;
    private static final int MAX_CONNECTIONS_PER_HOST = 30;
    private static final int CONNECTION_TIMEOUT_MILLIS = 2000;
    private static final int READ_TIMEOUT_MILLIS = 120000;

    public static final Client buildClient() {
        org.apache.http.client.HttpClient httpClient = createHttpClient(MAX_TOTAL_CONNECTIONS, MAX_CONNECTIONS_PER_HOST,
                CONNECTION_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS);

        ClientHandler clientHandler = new ApacheHttpClient4Handler(httpClient, null, false);
        ClientConfig config = new DefaultApacheHttpClient4Config();
        return new Client(clientHandler, config);
    }

    private static org.apache.http.client.HttpClient createHttpClient(int maxConnections, int defaultMaxPerRoute,
                                                                      int connectionTimeOut, int socketTimeout) {

        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        httpClientConnectionManager.setMaxTotal(maxConnections); // Increase max total connection to 200
        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute); // Increase default max connection per

        // route to 20
        /*
         * see http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt .html for an
         * explanation of these
         */
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeOut)
                .setSocketTimeout(socketTimeout).build();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(httpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        IdleConnectionMonitorThread idleConnectionMonitorThread = new IdleConnectionMonitorThread(httpClientConnectionManager);
        idleConnectionMonitorThread.setDaemon(true);
        idleConnectionMonitorThread.start();

        return httpClient;
    }


    public static class IdleConnectionMonitorThread extends Thread {
        private final HttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 30 sec
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }
}   
