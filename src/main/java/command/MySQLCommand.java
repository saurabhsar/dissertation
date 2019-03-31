package command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
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
//        super(HystrixCommandGroupKey.Factory.asKey("MySQL"));
        this.versioned = versioned;
        seed = System.currentTimeMillis()*100000l;
    }

//    @Override
    public String run() {
        if (versioned) {
            DI.di().getInstance(WithVersionDao.class).createRecord(WithVersion.builder().content("content").id(UUID.randomUUID().toString()).build());
        } else {
            DI.di().getInstance(WithoutVersionDao.class).createRecord(WithoutVersion.builder().content("content").id(UUID.randomUUID().toString()).build());
        }

        return null;
    }
}
