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
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRef;
import zeenea.connector.example.filter.Filter;
import zeenea.connector.example.json.Json;
import zeenea.connector.example.json.JsonParsingException;
import zeenea.connector.example.json.JsonProcess;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.example.property.CustomProperties;
import zeenea.connector.property.PropertyDefinition;
import zeenea.connector.synchronize.SynchronizeConnection;

public class ExampleLineageConnection implements SynchronizeConnection {
  private static final SimpleLogger log = SimpleLogger.of(ExampleLineageConnection.class);

  private final String connectionCode;
  private final FileFinder fileFinder;
  private final CustomProperties customProperties;
  private final Filter filter;

  public ExampleLineageConnection(
      String connectionCode,
      FileFinder fileFinder,
      CustomProperties customProperties,
      Filter filter) {
    this.connectionCode = Objects.requireNonNull(connectionCode);
    this.fileFinder = Objects.requireNonNull(fileFinder);
    this.customProperties = Objects.requireNonNull(customProperties);
    this.filter = Objects.requireNonNull(filter);
  }

  @Override
  public Stream<Item> synchronize() {
    TracingContext ctx = TracingContext.synchronize(connectionCode);
    var fileFilter = ItemFilters.fileFilter(filter);
    return fileFinder.findZeeneaFiles(ctx).stream()
        .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
        .flatMap(f -> readFile(ctx, f))
        .filter(p -> filter.matches(ItemFilters.item(p, customProperties)))
        .map(p -> ExampleMapper.dataProcess(p, customProperties));
  }

  public Stream<FileItem<JsonProcess>> readFile(TracingContext ctx, FileRef fileRef) {
    try {
      return Stream.ofNullable(Json.readFromFile(fileRef.getPath(), LineageRoot.class))
          .flatMap(root -> root.getLineage().stream())
          .map(p -> new FileItem<>(p, fileRef));

    } catch (JsonParsingException e) {
      log.entry("example_lineage_fail_to_read_file")
          .context(ctx)
          .with("path", fileRef.getPath())
          .error(e);
      return Stream.empty();
    }
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
