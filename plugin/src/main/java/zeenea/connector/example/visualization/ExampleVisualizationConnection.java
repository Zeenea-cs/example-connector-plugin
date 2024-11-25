package zeenea.connector.example.visualization;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import zeenea.connector.Item;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemInventory;
import zeenea.connector.example.Config;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.Ids;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileFinder;
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.json.Json;
import zeenea.connector.example.json.JsonVisualization;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.inventory.InventoryConnection;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.visualization.Visualization;

public class ExampleVisualizationConnection implements InventoryConnection {
  private static final SimpleLogger log = SimpleLogger.of(ExampleVisualizationConnection.class);

  /** Configuration. */
  private final Config config;

  /** FileFinder instance. */
  private final FileFinder fileFinder;

  /** Cache for extractItem item lookup. */
  private Map<ItemIdentifier, FileItem<JsonVisualization>> visualizationByItemId;

  /**
   * Create a new instance of {@code ExampleDatasetConnection}
   *
   * @param config The connection configuration.
   * @param fileFinder File finder instance.
   */
  public ExampleVisualizationConnection(Config config, FileFinder fileFinder) {
    this.config = Objects.requireNonNull(config);
    this.fileFinder = Objects.requireNonNull(fileFinder);
  }

  @Override
  public Set<PropertyDefinition> getProperties() {
    var properties = new HashSet<PropertyDefinition>();
    properties.add(Metadata.PATH_MD);
    properties.addAll(config.customProperties().getDefinitions());
    return properties;
  }

  @Override
  public Stream<ItemInventory> inventory() {
    // Create a context used by the logger.
    var ctx = TracingContext.inventory(config.connectionCode());
    log.entry("example_visualization_inventory_start").context(ctx).info();

    // Create a file partial filter to avoid reading files that could be filtered.
    var fileFilter = ItemFilters.fileFilter(config.filter());

    // We use an intermediate list to be able to count items.
    // There is a known issue that the returned stream is closed before being consumed which make
    // the use of onClose() useless to add post processing operation.
    List<ItemInventory> inventory =
        fileFinder.findZeeneaFiles(ctx).stream()
            .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
            .flatMap(f -> Json.readItems(ctx, f, JsonVisualization.class))
            .filter(p -> config.filter().matches(ItemFilters.item(p, config.customProperties())))
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
    var ctx = TracingContext.extractItems(config.connectionCode());
    log.entry("example_visualization_extract_items_start").context(ctx).info();

    loadVisualizations(ctx);

    return stream.filter(this::isVisualization).flatMap(id -> extractItem(ctx, id));
  }

  private Stream<Item> extractItem(TracingContext ctx, ItemIdentifier itemId) {
    log.entry("example_visualization_extract_item_start")
        .context(ctx)
        .with("item_id", Ids.log(itemId))
        .info();

    var fileItem = visualizationByItemId.get(itemId);
    if (fileItem == null) return Stream.empty();

    var item = fileItem.getItem();

    var visualization =
        Visualization.builder()
            .id(itemId)
            .name(item.getName())
            .description(item.getDescription())
            .properties(ExampleMapper.properties(fileItem, config.customProperties()))
            .contacts(ExampleMapper.contacts(item))
            .sourceDatasets(ExampleMapper.sources(item))
            .fields(ExampleMapper.fields(item, config.fieldProperties()))
            .build();

    return Stream.of(visualization);
  }

  private boolean isVisualization(ItemIdentifier itemId) {
    var idProps = itemId.getIdentificationProperties();
    if (idProps.isEmpty()) return false;
    var lastProp = idProps.get(idProps.size() - 1);
    return !lastProp.getKey().equals("embedded_type")
        || lastProp.getValue().equals("visualization");
  }

  private void loadVisualizations(TracingContext ctx) {
    if (visualizationByItemId == null) {
      // Create a file partial filter to avoid reading files that could be filtered.
      var fileFilter = ItemFilters.fileFilter(config.filter());
      visualizationByItemId =
          fileFinder.findZeeneaFiles(ctx).stream()
              .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
              .flatMap(f -> Json.readItems(ctx, f, JsonVisualization.class))
              .filter(v -> config.filter().matches(ItemFilters.item(v, config.customProperties())))
              .collect(
                  Collectors.toMap(
                      v -> ExampleMapper.parseItemId(v.getItem().getId()), Function.identity()));
    }
  }

  @Override
  public void close() {}
}
