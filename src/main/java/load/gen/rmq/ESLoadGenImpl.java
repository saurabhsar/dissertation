package load.gen.rmq;

import command.ElasticSearchCommand;
import load.gen.LoadGenI;
import resource.RequestType;

public class ESLoadGenImpl implements LoadGenI {

    private ElasticSearchCommand elasticSearchCommand = null;
    private RequestType requestType;

    @Override
    public void initialize(boolean transactional, boolean ignored, RequestType requestType) {
        this.requestType = requestType;
        //No Transaction in ES
    }

    @Override
    public void run() {

        elasticSearchCommand = new ElasticSearchCommand(requestType);

        try {
            elasticSearchCommand.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
