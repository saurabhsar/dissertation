package load.gen.mysql.dao;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.hibernate.AbstractDAO;
import load.gen.mysql.model.WithVersion;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WithVersionDao extends AbstractDAO<WithVersion> {
    private static Timer getLockedOfferTimer;
    private MetricRegistry metricRegistry;

    @Inject
    public WithVersionDao(SessionFactory sessionFactory, MetricRegistry metricRegistry) {
        super(sessionFactory);
        this.metricRegistry = metricRegistry;
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

}
