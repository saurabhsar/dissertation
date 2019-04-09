package command;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import di.DI;
import load.gen.elasticsearch.ESClient;
import resource.RequestType;

public class ElasticSearchCommand {
    private static ESClient esClient = null;
    private RequestType requestType;

    public ElasticSearchCommand(RequestType requestType) {
        if (esClient == null) {
            try {
                esClient = new ESClient(DI.di().getInstance(MetricRegistry.class));
            } finally {
                //ignore
            }
        }
        this.requestType = requestType;
    }

    @Timed
    public void run() throws Exception {
        if (RequestType.WRITE.equals(requestType)) {
            esClient.put();
        } else {
            esClient.get_v2();
        }
    }
}
