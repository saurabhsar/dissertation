package load.gen.mysql;

import command.MySQLCommand;
import load.gen.LoadGenI;

public class MySqlLoadGenImpl implements LoadGenI {

    private MySQLCommand mySQLCommand = null;
    private boolean versioned;

    @Override
    public void initialize(boolean versioned) {
        this.versioned = versioned;
    }

    @Override
    public void run() {

        mySQLCommand = new MySQLCommand(versioned);

        mySQLCommand.run();
    }
}
