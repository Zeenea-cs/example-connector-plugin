package zeenea.connector.example;

import static java.util.Objects.requireNonNull;

import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.property.CustomProperties;

/**
 * Configuration.
 *
 * <p>Use an object when there are may configuration entries.
 */
public final class ExampleConfig {
  private final String connectionCode;
  private final Filter filter;
  private final CustomProperties customProperties;
  private final CustomProperties fieldProperties;

  private ExampleConfig(Builder builder) {
    connectionCode = requireNonNull(builder.connectionCode);
    filter = requireNonNull(builder.filter);
    customProperties = requireNonNull(builder.customProperties);
    fieldProperties = builder.fieldProperties;
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

  public CustomProperties customProperties() {
    return customProperties;
  }

  public CustomProperties fieldProperties() {
    return fieldProperties;
  }

  public static class Builder {
    private String connectionCode;
    private Filter filter;
    private CustomProperties customProperties;
    private CustomProperties fieldProperties;

    public Builder connectionCode(String connectionCode) {
      this.connectionCode = connectionCode;
      return this;
    }

    public Builder filter(Filter filter) {
      this.filter = filter;
      return this;
    }

    public Builder customProperties(CustomProperties customProperties) {
      this.customProperties = customProperties;
      return this;
    }

    public Builder fieldProperties(CustomProperties fieldProperties) {
      this.fieldProperties = fieldProperties;
      return this;
    }

    public ExampleConfig build() {
      return new ExampleConfig(this);
    }
  }
}
