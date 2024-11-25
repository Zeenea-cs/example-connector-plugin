package zeenea.connector.example.lineage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import zeenea.connector.Item;
import zeenea.connector.example.Config;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.json.Json;
import zeenea.connector.example.json.JsonProcess;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.synchronize.SynchronizeConnection;

/**
 * Lineage connection.
 *
 * <p>This connection is a {@link SynchronizeConnection}.
 */
public class ExampleLineageConnection implements SynchronizeConnection {
  /** Logger instance. */
  private static final SimpleLogger log = SimpleLogger.of(ExampleLineageConnection.class);

  /** Configuration. */
  private final Config config;

  /** FileFinder instance. */
  private final FileFinder fileFinder;

  /**
   * Construct a new instance of {@code ExampleLineageConnection}.
   *
   * @param config The connection configuration.
   * @param fileFinder File finder instance.
   */
  public ExampleLineageConnection(Config config, FileFinder fileFinder) {
    this.config = Objects.requireNonNull(config);
    this.fileFinder = Objects.requireNonNull(fileFinder);
  }

  /**
   * List the source properties of this connection.
   *
   * @return The set of the connection's source properties.
   */
  @Override
  public Set<PropertyDefinition> getProperties() {
    var properties = new HashSet<PropertyDefinition>();
    properties.add(Metadata.PATH_MD);
    properties.addAll(config.customProperties().getDefinitions());
    return properties;
  }

  /**
   * Fetch the connection's items.
   *
   * @return A stream of Data process.
   */
  @Override
  public Stream<Item> synchronize() {
    // Create a context used by the logger.
    var ctx = TracingContext.synchronize(config.connectionCode());
    log.entry("example_lineage_synchronize_start").context(ctx).info();

    // Create a file partial filter to avoid reading files that could be filtered.
    var fileFilter = ItemFilters.fileFilter(config.filter());

    return fileFinder.findZeeneaFiles(ctx).stream()
        .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
        .flatMap(f -> Json.readItems(ctx, f, JsonProcess.class))
        .filter(p -> config.filter().matches(ItemFilters.item(p, config.customProperties())))
        .map(p -> ExampleMapper.dataProcess(p, config.customProperties()));
  }

  @Override
  public void close() {}
}
