package load.gen.mysql.dao;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.hibernate.AbstractDAO;
import load.gen.mysql.model.WithoutVersion;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WithoutVersionDao extends AbstractDAO<WithoutVersion> {
    private static Timer getLockedOfferTimer;
    private MetricRegistry metricRegistry;

    @Inject
    public WithoutVersionDao(SessionFactory sessionFactory, MetricRegistry metricRegistry) {
        super(sessionFactory);
        this.metricRegistry = metricRegistry;
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

}
