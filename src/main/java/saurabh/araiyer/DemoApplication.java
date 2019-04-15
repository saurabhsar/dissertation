package saurabh.araiyer;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import di.DI;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerDropwizard;
import load.gen.mysql.CustomHibernateBundle;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DemoApplication extends Application<AppConfiguration> {

    private final CustomHibernateBundle hibernateBundle = new CustomHibernateBundle();
    private final SwaggerDropwizard swaggerDropwizard = new SwaggerDropwizard();

    public static void main(String[] args) throws Exception {
        new DemoApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {

        bootstrap.addBundle(new MigrationsBundle<AppConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(AppConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });

        bootstrap.addBundle(hibernateBundle);

        GuiceBundle.Builder<AppConfiguration> guiceBundleBuilder = GuiceBundle.newBuilder();
        GuiceBundle<AppConfiguration> guiceBundle = guiceBundleBuilder
                .setConfigClass(AppConfiguration.class)
                .addModule(new AppModule())
//                .addModule(new AppJPAModule())
                .enableAutoConfig("resource")
                .build(Stage.DEVELOPMENT);

        bootstrap.addBundle(guiceBundle);
        DI.init(guiceBundle.getInjector());
        this.swaggerDropwizard.onInitialize(bootstrap);
    }

    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) throws UnknownHostException {

        configuration.setSessionFactory(hibernateBundle.getSessionFactory());
        configuration.setMetricRegistry(new MetricRegistry());
        final JmxReporter reporter = JmxReporter.forRegistry(DI.di().getInstance(MetricRegistry.class)).inDomain("dissertation").build();
        reporter.start();

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        String hostname = InetAddress.getLocalHost().getHostAddress();
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().setUrlPattern("/test/*");


        swaggerDropwizard.onRun(configuration, environment, hostname);
    }

}
