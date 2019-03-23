package load.gen;

import command.RMQCommand;

public class RMQLoadGenImpl implements LoadGenI {

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
