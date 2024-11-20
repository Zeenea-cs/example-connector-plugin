package zeenea.connector.example.dataset;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.Configuration;
import zeenea.connector.exception.InvalidConfigurationException;

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

    // Get file path for the configuration.
    var path = configuration.getPath(Configuration.PATH_CONF);

    // File path is relative to scanner home folder.
    var fullPath = path.isAbsolute() ? path : configuration.getScannerHomeFolder().resolve(path);

    return new ExampleDatasetConnection(connectionCode, fullPath);
  }
}
