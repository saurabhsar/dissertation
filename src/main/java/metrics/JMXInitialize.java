package metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import di.DI;

import javax.inject.Inject;

public class JMXInitialize {

  private MetricRegistry metricRegistry;

  @Inject
  public JMXInitialize(MetricRegistry metricRegistry){
    this.metricRegistry = metricRegistry;
    initialize();
  }

  private void initialize() {
    final JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
    reporter.start();
  }

}
