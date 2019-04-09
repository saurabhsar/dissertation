package load.gen.rmq;

import command.RMQCommand;
import load.gen.LoadGenI;
import resource.RequestType;

public class RMQLoadGenImpl implements LoadGenI {

    private RMQCommand rmqCommand = null;
    private boolean transactional;
    private boolean durable;
    private RequestType requestType;

    @Override
    public void initialize(boolean transactional, boolean durable, RequestType requestType) {
        this.transactional = transactional;
        this.durable = durable;
        this.requestType = requestType;
    }

    @Override
    public void run() {

        rmqCommand = new RMQCommand("RMQCommand", transactional, durable, requestType);

        try {
            rmqCommand.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
