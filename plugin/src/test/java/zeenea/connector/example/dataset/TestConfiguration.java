package zeenea.connector.example.dataset;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.exception.InvalidConfigurationException;

public class TestConfiguration implements ConnectionConfiguration {
  private final Path scannerHomeFolder;
  private final String connectorId;
  private final String connectionCode;
  private final String connectionName;
  private final Map<String, String> stringParameters;
  private final Map<String, Long> longParameters;
  private final Map<String, Boolean> boolParameters;
  private final Map<String, Path> pathParameters;

  private TestConfiguration(Builder builder) {
    this.scannerHomeFolder = builder.scannerHomeFolder;
    this.connectorId = builder.connectorId;
    this.connectionCode = builder.connectionCode;
    this.connectionName = builder.connectionName;
    this.stringParameters = Map.copyOf(builder.stringParameters);
    this.longParameters = Map.copyOf(builder.longParameters);
    this.boolParameters = Map.copyOf(builder.boolParameters);
    this.pathParameters = Map.copyOf(builder.pathParameters);
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Path getScannerHomeFolder() {
    return scannerHomeFolder;
  }

  @Override
  public String getConnectorId() {
    return connectorId;
  }

  @Override
  public String getConnectionName() {
    return connectionName;
  }

  @Override
  public String getConnectionCode() {
    return connectionCode;
  }

  @Override
  public String getString(String key) {
    return stringParameters.get(key);
  }

  @Override
  public Long getLong(String key) throws InvalidConfigurationException {
    return longParameters.get(key);
  }

  @Override
  public Boolean getBoolean(String key) throws InvalidConfigurationException {
    return boolParameters.get(key);
  }

  @Override
  public Path getPath(String key) throws InvalidConfigurationException {
    return pathParameters.get(key);
  }

  public static class Builder {
    private Path scannerHomeFolder = Path.of(".");
    private String connectorId;
    private String connectionCode;
    private String connectionName;
    private final Map<String, String> stringParameters = new HashMap<>();
    private final Map<String, Long> longParameters = new HashMap<>();
    private final Map<String, Boolean> boolParameters = new HashMap<>();
    private final Map<String, Path> pathParameters = new HashMap<>();

    public Builder scannerHomeFolder(Path scannerHomeFolder) {
      this.scannerHomeFolder = scannerHomeFolder;
      return this;
    }

    public Builder connectorId(String connectorId) {
      this.connectorId = connectorId;
      return this;
    }

    public Builder connectionCode(String connectionCode) {
      this.connectionCode = connectionCode;
      return this;
    }

    public Builder connectionName(String connectionName) {
      this.connectionName = connectionName;
      return this;
    }

    public Builder stringParam(String key, String value) {
      this.stringParameters.put(key, value);
      return this;
    }

    public Builder longParam(String key, Long value) {
      this.longParameters.put(key, value);
      return this;
    }

    public Builder boolParam(String key, Boolean value) {
      this.boolParameters.put(key, value);
      return this;
    }

    public Builder pathParam(String key, Path value) {
      this.pathParameters.put(key, value);
      return this;
    }

    public TestConfiguration build() {
      return new TestConfiguration(this);
    }
  }
}
