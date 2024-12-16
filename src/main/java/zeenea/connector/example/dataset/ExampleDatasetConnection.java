/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.dataset;

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
import zeenea.connector.dataset.Dataset;
import zeenea.connector.example.Config;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.Ids;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRepository;
import zeenea.connector.example.json.JsonDataset;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.inventory.InventoryConnection;
import zeenea.connector.property.PropertyDefinition;

public class ExampleDatasetConnection implements InventoryConnection {
  private static final SimpleLogger log = SimpleLogger.of(ExampleDatasetConnection.class);

  /** Configuration. */
  private final Config config;

  /** Mapper. */
  private final ExampleMapper mapper;

  /** FileFinder instance. */
  private final FileRepository fileRepository;

  /** Cache for extractItem item lookup. */
  private Map<ItemIdentifier, FileItem<JsonDataset>> datasetByItemId;

  /**
   * Create a new instance of {@code ExampleDatasetConnection}
   *
   * @param config The connection configuration.
   * @param fileRepository The file finder.
   */
  public ExampleDatasetConnection(
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
    properties.addAll(config.fieldProperties().getDefinitions());
    return properties;
  }

  @Override
  public Stream<ItemInventory> inventory() {
    // Create a context used by the logger.
    var ctx = TracingContext.inventory(config.connectionCode());
    log.entry("example_dataset_inventory_start")
        .context(ctx)
        .with("connection_code", config.connectionCode())
        .info();

    // We use an intermediate list to be able to count items.
    // There is a known issue that the returned stream is closed before being consumed which make
    // the use of onClose() useless to add post processing operation.
    List<ItemInventory> inventory =
        fileRepository
            .loadFileItems(ctx, JsonDataset.class)
            .map(
                d ->
                    ItemInventory.of(
                        mapper.parseItemId(d.getItem().getId()),
                        mapper.parseItemLabels(d.getItem())))
            .peek(
                i ->
                    log.entry("example_dataset_inventory_inventory_item_found")
                        .context(ctx)
                        .with("id", Ids.log(i.getItemIdentifier()))
                        .with("labels", Ids.logLabels(i.getLabels()))
                        .debug())
            .collect(Collectors.toList());

    log.entry("example_dataset_inventory_success")
        .context(ctx)
        .with("item_count", inventory.size())
        .info();

    return inventory.stream();
  }

  @Override
  public Stream<Item> extractItems(Stream<ItemIdentifier> stream) {
    var ctx = TracingContext.extractItems(config.connectionCode());
    log.entry("example_dataset_extract_items_start").context(ctx).info();

    loadDatasets(ctx);

    return stream.filter(this::isDataset).flatMap(id -> extractItem(ctx, id));
  }

  /**
   * Extract an item.
   *
   * <p>The result is provided as a stream that may be null if the id doesn't match any existing
   * item. This can happen if the item was remove from the source after the last inventory.
   *
   * @param ctx The tracing context.
   * @param itemId The item identifier.
   * @return A stream of datasets and processes. (Can be empty.)
   */
  private Stream<Item> extractItem(TracingContext ctx, ItemIdentifier itemId) {
    log.entry("example_dataset_extract_item_start")
        .context(ctx)
        .with("item_id", Ids.log(itemId))
        .info();

    var fileItem = datasetByItemId.get(itemId);
    if (fileItem == null) {
      // Item not found in the source.
      log.entry("example_dataset_extract_item_not_found")
          .context(ctx)
          .with("item_id", Ids.log(itemId))
          .warn();
      return Stream.empty();
    }

    var item = fileItem.getItem();

    var dataset =
        Dataset.builder()
            .id(itemId)
            .name(item.getName())
            .description(item.getDescription())
            .properties(mapper.properties(ctx, fileItem, config.customProperties()))
            .contacts(mapper.contacts(item))
            .sourceDatasets(mapper.itemReferences(item.getSources()))
            .fields(mapper.fields(ctx, item.getFields(), config.fieldProperties()))
            .primaryKeyIdentifiers(mapper.fieldIds(item.getPrimaryKey()))
            .foreignKeys(mapper.foreignKeys(item.getForeignKeys()))
            .build();

    return Stream.of(dataset);
  }

  /**
   * Filter out items that are not datasets (typically, lineage processes). They should not be
   * processed by the extractItem operation and will be updated/created by the processing of another
   * dataset.
   *
   * @param itemId Item id.
   * @return {@code true} if the item is a dataset to process.
   */
  private boolean isDataset(ItemIdentifier itemId) {
    var idProps = itemId.getIdentificationProperties();
    if (idProps.isEmpty()) return false;
    var lastProp = idProps.get(idProps.size() - 1);
    return !lastProp.getKey().equals("type");
  }

  /**
   * Load the datasets from the files.
   *
   * @param ctx Tracing context.
   */
  private void loadDatasets(TracingContext ctx) {
    if (datasetByItemId == null) {
      datasetByItemId =
          fileRepository
              .loadFileItems(ctx, JsonDataset.class)
              .collect(
                  Collectors.toMap(
                      v -> mapper.parseItemId(v.getItem().getId()), Function.identity()));
    }
  }

  @Override
  public void close() {}
}
