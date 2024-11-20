package zeenea.connector.example.lineage;

import org.pf4j.Extension;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Configuration;
import zeenea.connector.example.ItemFilters;
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

    // Get file path for the configuration.
    var path = configuration.getPath(Configuration.PATH_CONF);

    // File path is relative to scanner home folder.
    var fullPath = path.isAbsolute() ? path : configuration.getScannerHomeFolder().resolve(path);

    // Parse custom properties
    var customProperties =
        CustomProperties.parse(
            configuration.getStringOptional(Configuration.CUSTOM_PROPERTIES_CONF).orElse(""));

    // Parser Filter
    var filter = ItemFilters.parseFilter(configuration, customProperties);

    return new ExampleLineageConnection(fullPath, customProperties, filter);
  }
}
