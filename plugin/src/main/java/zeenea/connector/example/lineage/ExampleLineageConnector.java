package zeenea.connector.example.lineage;

import org.pf4j.Extension;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Configuration;
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

    // Create file finder.
    var fileFinder = FileFinder.create(configuration, "lineage");

    // Parse custom properties.
    var customProperties =
        CustomProperties.parse(
            configuration.getStringOptional(Configuration.CUSTOM_PROPERTIES_CONF).orElse(""));

    // Parser Filter.
    var filter = ItemFilters.parseFilter(configuration, customProperties);

    return new ExampleLineageConnection(connectionCode, fileFinder, customProperties, filter);
  }
}
