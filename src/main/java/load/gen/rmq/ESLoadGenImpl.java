package load.gen.rmq;

import command.RMQCommand;
import load.gen.LoadGenI;

public class ESLoadGenImpl implements LoadGenI {

    private RMQCommand rmqCommand = null;
    private boolean transactional;

    @Override
    public void initialize(boolean transactional) {
        this.transactional = transactional;
    }

    @Override
    public void run() {

        rmqCommand = new RMQCommand("RMQCommand", transactional);

        rmqCommand.execute();
    }
}
