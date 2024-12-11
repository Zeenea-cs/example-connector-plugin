package zeenea.connector.example.lineage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import zeenea.connector.Item;
import zeenea.connector.example.Config;
import zeenea.connector.example.ExampleMapper;
import zeenea.connector.example.Metadata;
import zeenea.connector.example.file.FileRepository;
import zeenea.connector.example.json.JsonProcess;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;
import zeenea.connector.process.DataProcess;
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

  /** Mapper. */
  private final ExampleMapper mapper;

  /** FileFinder instance. */
  private final FileRepository fileRepository;

  /**
   * Construct a new instance of {@code ExampleLineageConnection}.
   *
   * @param config The connection configuration.
   * @param fileRepository File finder instance.
   */
  public ExampleLineageConnection(
      Config config, ExampleMapper mapper, FileRepository fileRepository) {
    this.config = Objects.requireNonNull(config);
    this.mapper = Objects.requireNonNull(mapper);
    this.fileRepository = Objects.requireNonNull(fileRepository);
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

    return fileRepository
        .loadFileItems(ctx, JsonProcess.class)
        .map(
            fileItem -> {
              var process = fileItem.getItem();
              return DataProcess.builder()
                  .id(mapper.parseItemId(process.getId()))
                  .name(process.getName())
                  .description(process.getDescription())
                  .properties(mapper.properties(ctx, fileItem, config.customProperties()))
                  .contacts(mapper.contacts(process))
                  .sources(mapper.itemReferences(process.getSources()))
                  .targets(mapper.itemReferences(process.getTargets()))
                  .operations(mapper.operations(process.getOperations()))
                  .build();
            });
  }

  @Override
  public void close() {}
}
