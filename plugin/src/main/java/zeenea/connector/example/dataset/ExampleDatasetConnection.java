package zeenea.connector.example.dataset;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.Item;
import zeenea.connector.common.ItemIdentifier;
import zeenea.connector.common.ItemInventory;
import zeenea.connector.inventory.InventoryConnection;
import zeenea.connector.property.PropertyDefinition;

public class ExampleDatasetConnection implements InventoryConnection {
  private static final Logger log = LoggerFactory.getLogger(ExampleDatasetConnection.class);

  private final String connectionCode;
  private final Path fullPath;

  public ExampleDatasetConnection(String connectionCode, Path fullPath) {
    this.connectionCode = Objects.requireNonNull(connectionCode);
    this.fullPath = Objects.requireNonNull(fullPath);
  }

  @Override
  public Stream<ItemInventory> inventory() {
    return Stream.empty();
  }

  @Override
  public Stream<Item> extractItems(Stream<ItemIdentifier> stream) {
    return Stream.empty();
  }

  @Override
  public Set<PropertyDefinition> getProperties() {
    return Set.of();
  }

  @Override
  public void close() throws Exception {}
}
