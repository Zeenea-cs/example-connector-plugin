package zeenea.connector.example.dataset;

import org.pf4j.Extension;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Config;
import zeenea.connector.example.file.FileRepository;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.exception.InvalidConfigurationException;

@Extension
public class ExampleDatasetConnector implements Connector {
  private static final SimpleLogger log = SimpleLogger.of(ExampleDatasetConnector.class);

  @Override
  public String getConnectorId() {
    return "example-dataset";
  }

  @Override
  public ExampleDatasetConnection newConnection(ConnectionConfiguration configuration)
      throws InvalidConfigurationException {
    var ctx = TracingContext.newConnection(configuration.getConnectionCode());
    log.entry("example_dataset_new_connection").context(ctx).info();

    // The default extension is: ".dataset.ndjson".
    var config = Config.create(ctx, configuration, "dataset");

    // Create file finder.
    var fileFinder = new FileRepository(config);

    return new ExampleDatasetConnection(config, fileFinder);
  }
}
