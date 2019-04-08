package command;

import di.DI;
import load.gen.mysql.dao.WithVersionDao;
import load.gen.mysql.dao.WithoutVersionDao;
import load.gen.mysql.model.WithVersion;
import load.gen.mysql.model.WithoutVersion;

import java.util.UUID;


public class MySQLCommand {

    private boolean versioned;
    private Long seed;

    public MySQLCommand(boolean versioned) {
        this.versioned = versioned;
        seed = System.currentTimeMillis()*100000l;
    }

    public String write() {
        if (versioned) {
            DI.di().getInstance(WithVersionDao.class).createRecord(WithVersion.builder().content("content").id(UUID.randomUUID().toString()).build());
        } else {
            DI.di().getInstance(WithoutVersionDao.class).createRecord(WithoutVersion.builder().content("content").id(UUID.randomUUID().toString()).build());
        }

        return null;
    }
}
