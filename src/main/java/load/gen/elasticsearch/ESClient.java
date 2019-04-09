package load.gen.elasticsearch;

import client.ClientUtil;
import client.RequestUtils;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class ESClient {
    private static final String index = "load_test";
    private static Client client = null;
    private static final String clientName = "ES";
    private static final String headerContentType = "Content-Type";
    private static Counter counter;
    private static Timer timer;
    private static AtomicLong atomicLong = new AtomicLong();
    private static AtomicLong atomicLongCounter = new AtomicLong();
    private static Random random = new Random();
    private final String urlPut = "http://localhost:9200/load_test/post/%s";
    private TransportClient esclient;

    {
        try {
            esclient = new PreBuiltTransportClient(
                        Settings.builder().put("client.transport.sniff", true).put("cluster.name","elasticsearch").build())
                        .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    String urlGet = "http://localhost:9200/load_test/post/%s";

    @Inject
    public ESClient(MetricRegistry metricRegistry) {
        if (client == null) {
            client = ClientUtil.buildClient();
            counter = metricRegistry.counter("ESCounter");
            timer = metricRegistry.timer("ESTimer");
        }
    }

    public void put() {
        long begin = System.currentTimeMillis();

        String endpoint = String.format(urlPut, UUID.randomUUID());

        ClientResponse clientResponse = null;

        clientResponse = client.resource(endpoint)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header(headerContentType, MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, mockRequest());
        RequestUtils.handleErrorDefault(clientResponse, clientName);
        long end = System.currentTimeMillis();
        counter.inc();
        timer.update(end-begin, TimeUnit.MILLISECONDS);
    }

    public void get() {
        long begin = System.currentTimeMillis();

        String endpoint = String.format(urlGet, UUID.randomUUID());

        ClientResponse clientResponse;

        clientResponse = client.resource(endpoint)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header(headerContentType, MediaType.APPLICATION_JSON)
                .put(ClientResponse.class, getMockSearchQuery());
        RequestUtils.handleErrorDefault(clientResponse, clientName);
        long end = System.currentTimeMillis();
        counter.inc();
        timer.update(end-begin, TimeUnit.MILLISECONDS);
        System.out.println(atomicLongCounter.incrementAndGet());
    }

    private String mockRequest() {
        String mockRequest = "{\n" +
                "    \"user\" : \"%s\",\n" +
                "    \"message\" : \"This is test document\"\n" +
                "}";

        return String.format(mockRequest, atomicLong.incrementAndGet());
    }

    private String getMockSearchQuery() {
        String searchQuery = "{\n" +
                "    \"query\": {\n" +
                "        \"bool\": {\n" +
                "            \"must\": [\n" +
                "                {\n" +
                "                    \"match\": {\n" +
                "                        \"user\": {\n" +
                "                            \"query\": %s\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"from\": 0,\n" +
                "    \"size\": 1\n" +
                "}";
        int randomInt = random.nextInt()%5000;
        return String.format(searchQuery, randomInt > 0 ? randomInt : -randomInt);
    }

    public void get_v2() {
        int randomInt = random.nextInt()%5000;
        QueryBuilder matchSpecificFieldQuery= QueryBuilders.matchQuery("user", randomInt > 0 ? randomInt : -randomInt);
        esclient.prepareSearch()
                .setQuery(matchSpecificFieldQuery)
                .execute().actionGet();
    }
}
