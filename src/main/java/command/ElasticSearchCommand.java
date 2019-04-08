package command;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import di.DI;
import load.gen.elasticsearch.ESClient;

public class ElasticSearchCommand {
    private static ESClient esClient = null;

    public ElasticSearchCommand(String string) {
        if (esClient == null) {
            esClient = new ESClient(DI.di().getInstance(MetricRegistry.class));
        }
    }

    @Timed
    public String run() throws Exception {
        esClient.put();
        return null;
    }
}
