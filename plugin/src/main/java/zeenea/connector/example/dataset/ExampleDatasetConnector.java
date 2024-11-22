package zeenea.connector.example.dataset;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Configuration;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.exception.InvalidConfigurationException;

import static zeenea.connector.example.Configuration.DATASET_CUSTOM_PROPERTIES_CONF;
import static zeenea.connector.example.Configuration.FIELD_CUSTOM_PROPERTIES_CONF;
import static zeenea.connector.example.Configuration.PROCESS_CUSTOM_PROPERTIES_CONF;

@Extension
public class ExampleDatasetConnector implements Connector {
  private static final Logger log = LoggerFactory.getLogger(ExampleDatasetConnector.class);

  @Override
  public String getConnectorId() {
    return "example-dataset";
  }

  @Override
  public ExampleDatasetConnection newConnection(ConnectionConfiguration configuration)
      throws InvalidConfigurationException {

    // Get the connection code.
    var connectionCode = configuration.getConnectionCode();

    // Parse custom properties.
    var datasetProperties = CustomProperties.parse(configuration, DATASET_CUSTOM_PROPERTIES_CONF);
    var fieldProperties = CustomProperties.parse(FIELD_CUSTOM_PROPERTIES_CONF);
    var processProperties = CustomProperties.parse(configuration, PROCESS_CUSTOM_PROPERTIES_CONF);

    // Parser Filter.
    var filter = ItemFilters.parseFilter(configuration, datasetProperties);

    // Group the configuration in a single config object.
    var config =
        ExampleDatasetConfig.builder()
            .connectionCode(connectionCode)
            .filter(filter)
            .datasetProperties(datasetProperties)
            .fieldProperties(fieldProperties)
            .processProperties(processProperties)
            .build();

    // Create file finder. The default extension is: ".datasets.json".
    var fileFinder = FileFinder.create(configuration, "datasets");

    return new ExampleDatasetConnection(config, fileFinder);
  }
}
