package load.gen.mysql;

import com.google.common.collect.ImmutableList;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import load.gen.mysql.model.WithVersion;
import load.gen.mysql.model.WithoutVersion;
import saurabh.araiyer.AppConfiguration;

public class CustomHibernateBundle extends HibernateBundle<AppConfiguration> {

    public CustomHibernateBundle() {
        super(appEntities(), new SessionFactoryFactory());
    }

    @Override
    public DataSourceFactory getDataSourceFactory(AppConfiguration appConfiguration) {
        return appConfiguration.getDatabaseConfiguration();
    }

    private static ImmutableList<Class<?>> appEntities() {
        return (ImmutableList)ImmutableList.builder().add(WithVersion.class).add(WithoutVersion.class).build();
    }
}
