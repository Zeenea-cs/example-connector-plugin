package zeenea.connector.example;

import org.pf4j.Extension;
import zeenea.connector.Connection;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.Connector;
import zeenea.connector.example.repository.DatasourceRepository;
import zeenea.connector.exception.InvalidConfigurationException;

@Extension
public class ExampleConnector implements Connector {

  @Override
  public String getConnectorId() {
    // This value is the one that we will have to specify in the connector conf "connector_id"
    return "public-connector";
  }

  // By using the connector configuration, initialize the connection :
  // - verify credentials
  // - verify some elements does exist (database, site, etc.)
  // - verify everything that could show a bad configuration early
  @Override
  public Connection newConnection(ConnectionConfiguration connectionConfiguration)
      throws InvalidConfigurationException {
    String username =
        connectionConfiguration
            .getStringOptional(ExampleConfiguration.AUTHENTICATION_USERNAME)
            .orElseThrow(
                () -> new InvalidConfigurationException("Missing authentication username"));
    String password =
        connectionConfiguration
            .getStringOptional(ExampleConfiguration.AUTHENTICATION_PASSWORD)
            .orElseThrow(
                () -> new InvalidConfigurationException("Missing authentication password"));

    String nullable =
        connectionConfiguration.getString(ExampleConfiguration.SOME_NULLABLE_PROPERTY);
    Long optionalNumber =
        connectionConfiguration
            .getLongOptional(ExampleConfiguration.SOME_OPTIONAL_PROPERTY)
            .orElse(42L);
    Boolean flag =
        connectionConfiguration
            .getBooleanOptional(ExampleConfiguration.SOME_IMPORTANT_BOOLEAN)
            .orElse(Boolean.FALSE);

    DatasourceRepository repository =
        DatasourceRepository.builder()
            .username(username)
            .password(password)
            .nullable(nullable)
            .optionalNumber(optionalNumber)
            .flag(flag)
            .build();

    return new ExampleConnection(repository);
  }
}
