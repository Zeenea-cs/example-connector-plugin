package zeenea.connector.example.visualization;

import org.pf4j.Extension;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Config;
import zeenea.connector.example.file.FileRepository;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.exception.InvalidConfigurationException;

@Extension
public class ExampleVisualizationConnector implements Connector {
  private static final SimpleLogger log = SimpleLogger.of(ExampleVisualizationConnector.class);

  @Override
  public String getConnectorId() {
    return "example-visualization";
  }

  @Override
  public ExampleVisualizationConnection newConnection(ConnectionConfiguration configuration)
      throws InvalidConfigurationException {
    var ctx = TracingContext.newConnection(configuration.getConnectionCode());
    log.entry("example_visualization_new_connection").context(ctx).info();

    // The default extension is: ".visualization.ndjson".
    var config = Config.create(ctx, configuration, "visualization");

    // Create file finder.
    var fileFinder = new FileRepository(config);

    return new ExampleVisualizationConnection(config, fileFinder);
  }
}
