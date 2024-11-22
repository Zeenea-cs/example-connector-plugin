package zeenea.connector.example.visualization;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import zeenea.connector.Item;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemInventory;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.Ids;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.json.Json;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.inventory.InventoryConnection;
import zeenea.connector.property.PropertyDefinition;

public class ExampleVisualizationConnection implements InventoryConnection {
  private static final SimpleLogger log = SimpleLogger.of(ExampleVisualizationConnection.class);

  /** Connection code. */
  private final String connectionCode;

  /** Custom property repository. */
  private final CustomProperties customProperties;

  /** Item filter. */
  private final Filter filter;

  /** FileFinder instance. */
  private final FileFinder fileFinder;

  /**
   * Create a new instance of {@code ExampleDatasetConnection}
   *
   * @param connectionCode Connection code.
   * @param customProperties Custom property repository.
   * @param filter Item filter.
   * @param fileFinder File finder instance.
   */
  public ExampleVisualizationConnection(
      String connectionCode,
      CustomProperties customProperties,
      Filter filter,
      FileFinder fileFinder) {
    this.connectionCode = Objects.requireNonNull(connectionCode);
    this.fileFinder = Objects.requireNonNull(fileFinder);
    this.customProperties = Objects.requireNonNull(customProperties);
    this.filter = Objects.requireNonNull(filter);
  }

  @Override
  public Set<PropertyDefinition> getProperties() {
    var properties = new HashSet<PropertyDefinition>();
    properties.add(Metadata.PATH_MD);
    properties.addAll(customProperties.getDefinitions());
    return properties;
  }

  @Override
  public Stream<ItemInventory> inventory() {
    // Create a context used by the logger.
    var ctx = TracingContext.inventory(connectionCode);
    log.entry("example_visualization_inventory_start").context(ctx).info();

    // Create a file partial filter to avoid reading files that could be filtered.
    var fileFilter = ItemFilters.fileFilter(filter);

    // We use an intermediate list to be able to count items.
    // There is a known issue that the returned stream is closed before being consumed which make
    // the use of onClose() useless to add post processing operation.
    List<ItemInventory> inventory =
        fileFinder.findZeeneaFiles(ctx).stream()
            .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
            .flatMap(
                f ->
                    Json.readItems(
                        ctx, f, VisualizationRoot.class, VisualizationRoot::getVisualizations))
            .filter(p -> filter.matches(ItemFilters.item(p, customProperties)))
            .map(
                d ->
                    ItemInventory.of(
                        ExampleMapper.parseItemId(d.getItem().getId()),
                        ExampleMapper.parseItemLabels(d.getItem().getId())))
            .peek(
                i ->
                    log.entry("example_visualization_inventory_inventory_item_found")
                        .context(ctx)
                        .with("id", Ids.log(i.getItemIdentifier()))
                        .with("labels", Ids.logLabels(i.getLabels()))
                        .debug())
            .collect(Collectors.toList());

    log.entry("example_visualization_inventory_success")
        .context(ctx)
        .with("item_count", inventory.size())
        .info();

    return inventory.stream();
  }

  @Override
  public Stream<Item> extractItems(Stream<ItemIdentifier> stream) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public void close() {}
}
