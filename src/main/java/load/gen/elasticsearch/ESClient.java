package load.gen.elasticsearch;

import client.ClientUtil;
import client.RequestUtils;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ESClient {
    private static final String index = "load_test";
    private static Client client = null;
    private static final String clientName = "ES";
    private static final String headerContentType = "Content-Type";
    private static Counter counter;
    private static Timer timer;
    private final String urlPut = "http://localhost:9200/load_test/post/%s";

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

    private String mockRequest() {
        String mockRequest = "{\n" +
                "    \"user\" : \"%s\",\n" +
                "    \"message\" : \"This is test document\"\n" +
                "}";

        return String.format(mockRequest, UUID.randomUUID());
    }
}
