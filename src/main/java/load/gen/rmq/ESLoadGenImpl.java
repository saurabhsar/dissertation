package load.gen.rmq;

import command.ElasticSearchCommand;
import load.gen.LoadGenI;

public class ESLoadGenImpl implements LoadGenI {

    private ElasticSearchCommand elasticSearchCommand = null;

    @Override
    public void initialize(boolean transactional) {
        //No Transaction in ES
    }

    @Override
    public void run() {

        elasticSearchCommand = new ElasticSearchCommand("ESCommand");

        try {
            elasticSearchCommand.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
