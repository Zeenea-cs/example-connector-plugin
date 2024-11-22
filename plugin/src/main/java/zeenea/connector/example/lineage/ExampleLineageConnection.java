package zeenea.connector.example.lineage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import zeenea.connector.Item;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.json.Json;
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

  /** Connection code. */
  private final String connectionCode;

  /** Custom property repository. */
  private final CustomProperties customProperties;

  /** Item filter. */
  private final Filter filter;

  /** FileFinder instance. */
  private final FileFinder fileFinder;

  /**
   * Construct a new instance of {@code ExampleLineageConnection}.
   *
   * @param connectionCode Connection code.
   * @param customProperties Custom property repository.
   * @param filter Item filter.
   * @param fileFinder File finder instance.
   */
  public ExampleLineageConnection(
      String connectionCode,
      CustomProperties customProperties,
      Filter filter,
      FileFinder fileFinder) {
    this.connectionCode = Objects.requireNonNull(connectionCode);
    this.fileFinder = Objects.requireNonNull(fileFinder);
    this.customProperties = Objects.requireNonNull(customProperties);
    this.filter = Objects.requireNonNull(filter);
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
    properties.addAll(customProperties.getDefinitions());
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
    var ctx = TracingContext.synchronize(connectionCode);
    log.entry("example_lineage_synchronize_start").context(ctx).info();

    // Create a file partial filter to avoid reading files that could be filtered.
    var fileFilter = ItemFilters.fileFilter(filter);

    return fileFinder.findZeeneaFiles(ctx).stream()
        .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
        .flatMap(f -> Json.readItems(ctx, f, LineageRoot.class, LineageRoot::getLineage))
        .filter(p -> filter.matches(ItemFilters.item(p, customProperties)))
        .map(p -> ExampleMapper.dataProcess(p, customProperties));
  }

  @Override
  public void close() {}
}
