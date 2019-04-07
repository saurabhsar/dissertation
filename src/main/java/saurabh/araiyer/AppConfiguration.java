package saurabh.araiyer;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;
import org.hibernate.SessionFactory;
import org.hibernate.validator.constraints.NotEmpty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class AppConfiguration extends Configuration {
    @NotEmpty
    private String template = "template";

    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory databaseConfiguration = new DataSourceFactory();

    private SessionFactory sessionFactory;

    private MetricRegistry metricRegistry;
}
