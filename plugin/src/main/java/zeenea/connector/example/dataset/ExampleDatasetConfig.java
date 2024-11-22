package zeenea.connector.example.dataset;

import static java.util.Objects.requireNonNull;

import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.property.CustomProperties;

/**
 * Configuration of the {@code ExampleDatasetConnection}.
 *
 * <p>Use an object when there are may configuration entries.
 */
public final class ExampleDatasetConfig {
  private final String connectionCode;
  private final Filter filter;
  private final CustomProperties datasetProperties;
  private final CustomProperties fieldProperties;
  private final CustomProperties processProperties;

  private ExampleDatasetConfig(Builder builder) {
    connectionCode = requireNonNull(builder.connectionCode);
    filter = requireNonNull(builder.filter);
    datasetProperties = requireNonNull(builder.datasetProperties);
    fieldProperties = requireNonNull(builder.fieldProperties);
    processProperties = requireNonNull(builder.processProperties);
  }

  static Builder builder() {
    return new Builder();
  }

  public String connectionCode() {
    return connectionCode;
  }

  public Filter filter() {
    return filter;
  }

  public CustomProperties datasetProperties() {
    return datasetProperties;
  }

  public CustomProperties fieldProperties() {
    return fieldProperties;
  }

  public CustomProperties processProperties() {
    return processProperties;
  }

  public static class Builder {
    private String connectionCode;
    private Filter filter;
    private CustomProperties datasetProperties;
    private CustomProperties fieldProperties;
    private CustomProperties processProperties;

    public Builder connectionCode(String connectionCode) {
      this.connectionCode = connectionCode;
      return this;
    }

    public Builder filter(Filter filter) {
      this.filter = filter;
      return this;
    }

    public Builder datasetProperties(CustomProperties datasetProperties) {
      this.datasetProperties = datasetProperties;
      return this;
    }

    public Builder fieldProperties(CustomProperties fieldProperties) {
      this.fieldProperties = fieldProperties;
      return this;
    }

    public Builder processProperties(CustomProperties processProperties) {
      this.processProperties = processProperties;
      return this;
    }

    public ExampleDatasetConfig build() {
      return new ExampleDatasetConfig(this);
    }
  }
}
