package zeenea.connector.example;

import java.nio.file.Path;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.log.Strings;
import zeenea.connector.example.log.TracingContext;
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
  private final Path root;
  private final String fileExtension;
  private final CustomProperties customProperties;
  private final CustomProperties fieldProperties;
  private final Filter filter;

  private Config(
      String connectionCode,
      Path root,
      String fileExtension,
      CustomProperties customProperties,
      CustomProperties fieldProperties,
      Filter filter) {
    this.connectionCode = connectionCode;
    this.root = root;
    this.fileExtension = fileExtension;
    this.customProperties = customProperties;
    this.fieldProperties = fieldProperties;
    this.filter = filter;
  }

  public static Config create(
      TracingContext ctx, ConnectionConfiguration configuration, String defaultExtension) {
    // Connection code
    var connectionCode = configuration.getConnectionCode();

    // Get file path for the configuration.
    var path = configuration.getPath(PATH_CONF);

    // File path is relative to scanner home folder.
    var fullPath = path.isAbsolute() ? path : configuration.getScannerHomeFolder().resolve(path);

    // Get the extension.
    var extension =
        configuration
            .getStringOptional(EXTENSION_CONF)
            .map(e -> Strings.ensurePrefix(".", e))
            .orElse(Strings.ensurePrefix(".", defaultExtension + ".ndjson"));

    // Parse custom properties.
    var customProperties =
        CustomProperties.parse(configuration.getStringOptional(CUSTOM_PROPERTIES_CONF).orElse(""));

    // Parse field custom properties.
    var fieldProperties =
        CustomProperties.parse(
            configuration.getStringOptional(FIELD_CUSTOM_PROPERTIES_CONF).orElse(""));

    // Parser Filter.
    var filter = ItemFilters.parseFilter(configuration, customProperties);

    return new Config(
        connectionCode, fullPath, extension, customProperties, fieldProperties, filter);
  }

  public String connectionCode() {
    return connectionCode;
  }

  public Path root() {
    return root;
  }

  public String fileExtension() {
    return fileExtension;
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
}
