package command;

import di.DI;
import load.gen.mysql.dao.WithVersionDao;
import load.gen.mysql.dao.WithoutVersionDao;
import load.gen.mysql.model.WithVersion;
import load.gen.mysql.model.WithoutVersion;
import resource.RequestType;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MySQLCommand {

    private boolean versioned;
    private RequestType requestType;
    private static AtomicLong atomicLong = null;

    public MySQLCommand(boolean versioned, RequestType requestType) {
        this.versioned = versioned;
        this.requestType = requestType;
        if (atomicLong == null) {
            atomicLong = new AtomicLong();
        }
    }

    public void perform() {
        if (RequestType.WRITE.equals(requestType)) {
            if (versioned) {
                DI.di().getInstance(WithVersionDao.class).createRecord(WithVersion.builder()
                        .content("content").id(UUID.randomUUID().toString()).build());
            } else {
                DI.di().getInstance(WithoutVersionDao.class).createRecord(WithoutVersion.builder()
                        .content("content").id(UUID.randomUUID().toString()).build());
            }
        } else {
            if (versioned) {
                DI.di().getInstance(WithVersionDao.class).readRandom();
            } else {
                DI.di().getInstance(WithoutVersionDao.class).readRandom();
            }
        }
        System.out.println(atomicLong.incrementAndGet());
    }
}
