package zeenea.connector.example;

import static java.util.Objects.requireNonNull;

import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.property.CustomProperties;

/**
 * Configuration.
 *
 * <p>Use an object when there are may configuration entries.
 */
public class Config {
  public static final String FILTER_CONF = "filter";
  public static final String PATH_CONF = "path";
  public static final String EXTENSION_CONF = "extension";
  public static final String CUSTOM_PROPERTIES_CONF = "custom_properties";
  public static final String FIELD_CUSTOM_PROPERTIES_CONF = "field_custom_properties";

  private final String connectionCode;
  private final Filter filter;
  private final CustomProperties customProperties;
  private final CustomProperties fieldProperties;

  private Config(Builder builder) {
    connectionCode = requireNonNull(builder.connectionCode);
    filter = requireNonNull(builder.filter);
    customProperties = requireNonNull(builder.customProperties);
    fieldProperties = requireNonNull(builder.fieldProperties);
  }

  public static Builder builder() {
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
    private CustomProperties customProperties = CustomProperties.empty();
    private CustomProperties fieldProperties = CustomProperties.empty();

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

    public Config build() {
      return new Config(this);
    }
  }
}
