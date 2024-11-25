package zeenea.connector.example.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import zeenea.connector.example.Config;
import zeenea.connector.example.ItemFilters;
import zeenea.connector.example.json.Json;
import zeenea.connector.example.json.JsonItem;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.TracingContext;

public class FileRepository {
  private static final SimpleLogger log = SimpleLogger.of(FileRepository.class);

  private final Config config;

  public FileRepository(Config config) {
    this.config = Objects.requireNonNull(config);
  }

  public <E extends JsonItem> Stream<FileItem<E>> loadFileItems(
      TracingContext ctx, Class<E> klass) {
    // Create a file partial filter to avoid reading files that could be filtered.
    var fileFilter = ItemFilters.fileFilter(config.filter());

    return findZeeneaFiles(ctx).stream()
        .filter(f -> fileFilter.matches(ItemFilters.fileItem(f)))
        .flatMap(f -> Json.readItems(ctx, f, klass))
        .filter(v -> config.filter().matches(ItemFilters.item(v, config.customProperties())));
  }

  /**
   * Find Zeenea files. A Zeenea file is a file with the extension.
   *
   * @param ctx Tracing context.
   * @return A list of zeenea file references.
   */
  public List<FileRef> findZeeneaFiles(TracingContext ctx) {
    var root = config.root();
    var extension = config.fileExtension();
    Path fileName = root.getFileName();
    if (fileName != null && fileName.toString().endsWith(extension) && Files.isRegularFile(root)) {
      Path parent = root.getParent();
      if (parent == null) {
        parent = Path.of("");
      }
      return List.of(new FileRef(root, parent));
    }
    try (var files = Files.walk(root)) {
      return files
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith(extension))
          .map(p -> new FileRef(p, root))
          .collect(Collectors.toUnmodifiableList());
    } catch (IOException e) {
      throw log.entry("zdf_find_zeenea_files_failure")
          .context(ctx)
          .with("root", root)
          .exception(e, FindFileException::new);
    }
  }
}
