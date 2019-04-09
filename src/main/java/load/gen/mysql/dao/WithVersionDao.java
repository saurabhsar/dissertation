package load.gen.mysql.dao;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.hibernate.AbstractDAO;
import load.gen.mysql.model.WithVersion;
import org.hibernate.CacheMode;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Random;

@Singleton
public class WithVersionDao extends AbstractDAO<WithVersion> {
    private static Timer getLockedOfferTimer;
    private MetricRegistry metricRegistry;
    private static List keys;
    private static Random random = new Random();
    private static final String getQuery = "select * from with_version where id = \"";
    private static final String close = "\"";

    @Inject
    public WithVersionDao(SessionFactory sessionFactory, MetricRegistry metricRegistry) {
        super(sessionFactory);
        this.metricRegistry = metricRegistry;
        initializeMetrics();
    }

    private void initializeMetrics() {
        if (getLockedOfferTimer == null) {
            getLockedOfferTimer = metricRegistry.timer("GetLockedOfferTimer");
        }
    }

    public WithVersion createRecord(WithVersion entity) {
        this.currentSession().save(entity);
        return entity;
    }

    public WithVersion updateRecord(WithVersion entity) {
        this.currentSession().saveOrUpdate(entity);
        return entity;
    }

    public void readRandom() {
        getAllKeys();
        int var = random.nextInt();
        var = var > 0 ? var : -var;
        String key_to_fetch = (String) keys.get(var % keys.size());
        SQLQuery sqlQuery = this.currentSession().createSQLQuery(getQuery + key_to_fetch + close);
        this.currentSession().setCacheMode(CacheMode.IGNORE);
        sqlQuery.list().size();
        this.currentSession().clear();
    }

    private void getAllKeys() {
        if (keys == null || keys.size() == 0) {
            SQLQuery query = this.currentSession().createSQLQuery("select id from with_version");
            keys = query.list();
        }
    }
}
