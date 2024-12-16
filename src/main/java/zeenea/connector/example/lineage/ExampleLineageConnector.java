/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.lineage;

import org.pf4j.Extension;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Config;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.file.FileRepository;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.exception.InvalidConfigurationException;

@Extension
public class ExampleLineageConnector implements Connector {
  private static final SimpleLogger log = SimpleLogger.of(ExampleLineageConnector.class);

  @Override
  public String getConnectorId() {
    return "example-lineage";
  }

  @Override
  public ExampleLineageConnection newConnection(ConnectionConfiguration configuration)
      throws InvalidConfigurationException {
    var ctx = TracingContext.newConnection(configuration.getConnectionCode());
    log.entry("example_lineage_new_connection").context(ctx).info();

    // The default extension is: ".lineage.ndjson".
    var config = Config.create(ctx, configuration, "lineage");

    var mapper = new ExampleMapper(config.connectionCode());

    // Create file finder.
    var fileFinder = new FileRepository(config);

    return new ExampleLineageConnection(config, mapper, fileFinder);
  }
}
