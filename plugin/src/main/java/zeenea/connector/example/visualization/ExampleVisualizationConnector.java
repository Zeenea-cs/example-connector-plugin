package zeenea.connector.example.visualization;

import static zeenea.connector.example.Configuration.DATASET_CUSTOM_PROPERTIES_CONF;
import static zeenea.connector.example.Configuration.FIELD_CUSTOM_PROPERTIES_CONF;
import static zeenea.connector.example.Configuration.PROCESS_CUSTOM_PROPERTIES_CONF;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Configuration;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.dataset.ExampleDatasetConfig;
import zeenea.connector.example.dataset.ExampleDatasetConnection;
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
    var customProperties =
        CustomProperties.parse(
            configuration.getStringOptional(Configuration.CUSTOM_PROPERTIES_CONF).orElse(""));

    // Parser Filter.
    var filter = ItemFilters.parseFilter(configuration, customProperties);

    // Create file finder. The default extension is: ".visualization.json".
    var fileFinder = FileFinder.create(configuration, "visualization");

    return new ExampleVisualizationConnection(connectionCode, customProperties, filter, fileFinder);
  }
}
