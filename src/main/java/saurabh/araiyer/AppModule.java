package saurabh.araiyer;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import load.gen.elasticsearch.ESClient;
import load.gen.mysql.dao.WithVersionDao;
import load.gen.mysql.dao.WithoutVersionDao;
import metrics.JMXInitialize;
import org.hibernate.SessionFactory;
import resource.LoadGeneratorResource;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        this.bind(WithoutVersionDao.class).in(Singleton.class);
        this.bind(WithVersionDao.class).in(Singleton.class);
        this.bind(LoadGeneratorResource.class).in(Singleton.class);
        bind(JMXInitialize.class).in(Singleton.class);
        bind(ESClient.class).in(Singleton.class);
    }

    @Provides
    SessionFactory providesSessionFactory(Provider<AppConfiguration> configurationProvider) {
        return configurationProvider.get().getSessionFactory();
    }

    @Provides
    MetricRegistry metricRegistry(Provider<AppConfiguration> configurationProvider) {
        return configurationProvider.get().getMetricRegistry();
    }
}
