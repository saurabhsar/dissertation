package saurabh.araiyer;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resource.LoadGeneratorResourcee;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DemoApplication extends Application<AppConfiguration> {

    public static void main(String[] args) throws Exception {
        new DemoApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        // nothing to do yet
//        GuiceBundle<AppConfiguration> guiceBundle =
    }

    @Override
    public void run(AppConfiguration configuration,
                    Environment environment) throws UnknownHostException {
        final LoadGeneratorResourcee resource = new LoadGeneratorResourcee(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        String hostname = InetAddress.getLocalHost().getHostAddress();
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().setUrlPattern("/test/*");

        environment.jersey().register(resource);
    }

}
