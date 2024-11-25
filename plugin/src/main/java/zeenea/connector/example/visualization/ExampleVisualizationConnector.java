package zeenea.connector.example.visualization;

import static zeenea.connector.example.Config.CUSTOM_PROPERTIES_CONF;
import static zeenea.connector.example.Config.FIELD_CUSTOM_PROPERTIES_CONF;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Config;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.exception.InvalidConfigurationException;

@Extension
public class ExampleVisualizationConnector implements Connector {
  private static final Logger log = LoggerFactory.getLogger(ExampleVisualizationConnector.class);

  @Override
  public String getConnectorId() {
    return "example-visualization";
  }

  @Override
  public ExampleVisualizationConnection newConnection(ConnectionConfiguration configuration)
      throws InvalidConfigurationException {

    // Connection code
    var connectionCode = configuration.getConnectionCode();

    // Parse custom properties.
    var customProperties = CustomProperties.parse(CUSTOM_PROPERTIES_CONF);
    var fieldProperties = CustomProperties.parse(FIELD_CUSTOM_PROPERTIES_CONF);

    // Parser Filter.
    var filter = ItemFilters.parseFilter(configuration, customProperties);

    // Group the configuration in a single config object.
    var config =
        Config.builder()
            .connectionCode(connectionCode)
            .filter(filter)
            .customProperties(customProperties)
            .fieldProperties(fieldProperties)
            .build();

    // Create file finder. The default extension is: ".visualization.ndjson".
    var fileFinder = FileFinder.create(configuration, "visualization");

    return new ExampleVisualizationConnection(config, fileFinder);
  }
}
