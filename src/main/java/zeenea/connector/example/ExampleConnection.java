package zeenea.connector.example;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.Item;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemInventory;
import zeenea.connector.datasource.DataSource;
import zeenea.connector.example.mapper.IdentifierMapper;
import zeenea.connector.example.mapper.ItemMapper;
import zeenea.connector.example.repository.DatasourceRepository;
import zeenea.connector.example.repository.models.DatasourceElement;
import zeenea.connector.inventory.InventoryConnection;
import zeenea.connector.property.PropertyDefinition;

public class ExampleConnection implements InventoryConnection {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleConnection.class);

  private final DatasourceRepository repository;

  public ExampleConnection(DatasourceRepository repository) {
    this.repository = repository;
  }

  // Method called to get the DataSource information, like host, port, database name...
  @Override
  public Optional<DataSource> getDataSource() {
    return Optional.empty();
  }

  // Method called to list every items available in the source system
  @Override
  public Stream<ItemInventory> inventory() {
    LOGGER.info("Starting inventory");
    AtomicInteger counter = new AtomicInteger();
    return repository.listAllItems().stream()
        .map(IdentifierMapper::mapToItemInventory)
        .peek(e -> counter.incrementAndGet())
        .onClose(() -> LOGGER.info("Inventory completed, {} items found", counter.get()));
  }

  // Method called to extract items from the source system given their identifiers
  // Because one item can potentially have embedded elements, the extract of one item can result in
  // the extraction of multiple items
  // It is the reason why the return type is a Stream<Item>
  @Override
  public Stream<Item> extractItems(Stream<ItemIdentifier> identifiers) {
    return identifiers.flatMap(this::extractItem);
  }

  public Stream<Item> extractItem(ItemIdentifier itemIdentifier) {
    LOGGER.info("Extracting item {}", itemIdentifier);

    String databaseId =
        IdentifierMapper.getDatabaseId(itemIdentifier)
            .orElseThrow(
                () -> new IllegalArgumentException("Missing databaseId in item identifier"));
    String id =
        IdentifierMapper.getId(itemIdentifier)
            .orElseThrow(() -> new IllegalArgumentException("Missing id in item identifier"));

    DatasourceElement datasourceElement = repository.find(databaseId, id);
    Item item = ItemMapper.mapToItem(datasourceElement);

    LOGGER.info("Extraction succeeded for item {}", itemIdentifier);

    return Stream.of(item);
  }

  // Item metadata must be defined by the connection before they can be set
  // The values will be shown in the item page in Studio under "Source properties" section
  @Override
  public Set<PropertyDefinition> getProperties() {
    return ExampleMetadata.getPropertyDefinitions();
  }

  // Method called when the connection is closed
  @Override
  public void close() {
    repository.close();
  }
}
