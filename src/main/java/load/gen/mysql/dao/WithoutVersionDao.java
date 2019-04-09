package load.gen.mysql.dao;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.hibernate.AbstractDAO;
import load.gen.mysql.model.WithoutVersion;
import org.hibernate.CacheMode;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Random;

@Singleton
public class WithoutVersionDao extends AbstractDAO<WithoutVersion> {
    private static Timer getLockedOfferTimer;
    private MetricRegistry metricRegistry;
    private static List keys;
    private static Random random = new Random();
    private static final String getQuery = "select * from without_version where id = \"";
    private static final String close = "\"";


    @Inject
    public WithoutVersionDao(SessionFactory sessionFactory, MetricRegistry metricRegistry) {
        super(sessionFactory);
        this.metricRegistry = metricRegistry;
        initializeMetrics();
    }

    private void initializeMetrics() {
        if (getLockedOfferTimer == null) {
            getLockedOfferTimer = metricRegistry.timer("GetLockedOfferTimer");
        }
    }

    public WithoutVersion createRecord(WithoutVersion entity) {
        this.currentSession().save(entity);
        return entity;
    }

    public void readRandom() {
        getAllKeys();
        int var = random.nextInt();
        var = var < 0 ? -var : var;
        String key_to_fetch = (String ) keys.get(var % keys.size());
        SQLQuery sqlQuery = this.currentSession().createSQLQuery(getQuery + key_to_fetch + close + "");
        this.currentSession().setCacheMode(CacheMode.IGNORE);
        this.currentSession().clear();
        sqlQuery.list().size();
    }

    private void getAllKeys() {
        if (keys == null || keys.size() == 0) {
            SQLQuery query = this.currentSession().createSQLQuery("select id from without_version");
            keys = query.list();
        }
    }
}
