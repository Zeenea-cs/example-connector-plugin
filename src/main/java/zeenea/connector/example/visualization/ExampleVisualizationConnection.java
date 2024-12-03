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
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRepository;
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

  /** Mapper. */
  private final ExampleMapper mapper;

  /** FileFinder instance. */
  private final FileRepository fileRepository;

  /** Cache for extractItem item lookup. */
  private Map<ItemIdentifier, FileItem<JsonVisualization>> visualizationByItemId;

  /**
   * Create a new instance of {@code ExampleDatasetConnection}
   *
   * @param config The connection configuration.
   * @param fileRepository File finder instance.
   */
  public ExampleVisualizationConnection(
      Config config, ExampleMapper mapper, FileRepository fileRepository) {
    this.config = Objects.requireNonNull(config);
    this.mapper = Objects.requireNonNull(mapper);
    this.fileRepository = Objects.requireNonNull(fileRepository);
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

    // We use an intermediate list to be able to count items.
    // There is a known issue that the returned stream is closed before being consumed which make
    // the use of onClose() useless to add post processing operation.
    List<ItemInventory> inventory =
        fileRepository
            .loadFileItems(ctx, JsonVisualization.class)
            .map(
                d ->
                    ItemInventory.of(
                        mapper.parseItemId(d.getItem().getId()),
                        mapper.parseItemLabels(d.getItem())))
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

  /**
   * Extract an item.
   *
   * <p>The result is provided as a stream that may be null if the id doesn't match any existing
   * item. This can happen if the item was remove from the source after the last inventory.
   *
   * @param ctx The tracing context.
   * @param itemId The item identifier.
   * @return A stream of visualisations. (Can be empty.)
   */
  private Stream<Item> extractItem(TracingContext ctx, ItemIdentifier itemId) {
    log.entry("example_visualization_extract_item_start")
        .context(ctx)
        .with("item_id", Ids.log(itemId))
        .info();

    var fileItem = visualizationByItemId.get(itemId);
    if (fileItem == null) {
      // Item not found in the source.
      log.entry("example_visualization_extract_item_not_found")
          .context(ctx)
          .with("item_id", Ids.log(itemId))
          .warn();
      return Stream.empty();
    }

    var item = fileItem.getItem();

    var visualization =
        Visualization.builder()
            .id(itemId)
            .name(item.getName())
            .description(item.getDescription())
            .properties(mapper.properties(ctx, fileItem, config.customProperties()))
            .contacts(mapper.contacts(item))
            .sourceDatasets(mapper.itemReferences(item.getSources()))
            .fields(mapper.fields(ctx, item.getFields(), config.fieldProperties()))
            .build();

    return Stream.of(visualization);
  }

  /**
   * Filter out embedded items automatically created by the platform.
   *
   * @param itemId Item id.
   * @return {@code true} if the item is a visualization.
   */
  private boolean isVisualization(ItemIdentifier itemId) {
    var idProps = itemId.getIdentificationProperties();
    if (idProps.isEmpty()) return false;
    var lastProp = idProps.get(idProps.size() - 1);
    return !lastProp.getKey().equals("embedded_type");
  }

  /**
   * Load the visualizations from the files.
   *
   * @param ctx Tracing context.
   */
  private void loadVisualizations(TracingContext ctx) {
    if (visualizationByItemId == null) {
      visualizationByItemId =
          fileRepository
              .loadFileItems(ctx, JsonVisualization.class)
              .collect(
                  Collectors.toMap(
                      v -> mapper.parseItemId(v.getItem().getId()), Function.identity()));
    }
  }

  @Override
  public void close() {}
}
