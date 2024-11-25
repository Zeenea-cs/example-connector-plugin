package zeenea.connector.example.lineage;

import org.pf4j.Extension;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Config;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.exception.InvalidConfigurationException;

@Extension
public class ExampleLineageConnector implements Connector {
  @Override
  public String getConnectorId() {
    return "example-dataset";
  }

  @Override
  public ExampleLineageConnection newConnection(ConnectionConfiguration configuration)
      throws InvalidConfigurationException {

    // Connection code
    var connectionCode = configuration.getConnectionCode();

    // Parse custom properties.
    var customProperties =
            CustomProperties.parse(
                    configuration.getStringOptional(Config.CUSTOM_PROPERTIES_CONF).orElse(""));

    // Parser Filter.
    var filter = ItemFilters.parseFilter(configuration, customProperties);

    // Group the configuration in a single config object.
    var config =
            Config.builder()
                    .connectionCode(connectionCode)
                    .filter(filter)
                    .customProperties(customProperties)
                    .build();

    // Create file finder. The default extension is: ".lineage.ndjson".
    var fileFinder = FileFinder.create(configuration, "lineage");

    return new ExampleLineageConnection(config, fileFinder);
  }
}
