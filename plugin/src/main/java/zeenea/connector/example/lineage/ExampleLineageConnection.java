package zeenea.connector.example.lineage;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zeenea.connector.Item;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.json.Json;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.synchronize.SynchronizeConnection;

public class ExampleLineageConnection implements SynchronizeConnection {
  private static final Logger log = LoggerFactory.getLogger(ExampleLineageConnection.class);

  private final Path fullPath;
  private final CustomProperties customProperties;
  private final Filter filter;

  public ExampleLineageConnection(Path fullPath, CustomProperties customProperties, Filter filter) {
    this.fullPath = Objects.requireNonNull(fullPath);
    this.customProperties = Objects.requireNonNull(customProperties);
    this.filter = Objects.requireNonNull(filter);
  }

  @Override
  public Stream<Item> synchronize() {
    var root = Json.readFromFile(fullPath, LineageRoot.class);
    return root.getLineage().stream()
        .filter(p -> filter.matches(ItemFilters.item(p, customProperties)))
        .map(p -> ExampleMapper.dataProcess(p, fullPath, customProperties));
  }

  @Override
  public Set<PropertyDefinition> getProperties() {
    var properties = new HashSet<PropertyDefinition>();
    properties.add(Metadata.PATH_MD);
    properties.addAll(customProperties.getDefinitions());
    return properties;
  }

  @Override
  public void close() {}
}
