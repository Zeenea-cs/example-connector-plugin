package zeenea.connector.example.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import zeenea.connector.ConnectionConfiguration;
import zeenea.connector.example.Configuration;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.Strings;
import zeenea.connector.example.log.TracingContext;

public class FileFinder {
  private static final SimpleLogger log = SimpleLogger.of(FileFinder.class);

  private final Path root;
  private final String extension;

  public FileFinder(Path root, String extension) {
    this.root = Objects.requireNonNull(root);
    this.extension = Objects.requireNonNull(extension);
  }

  /**
   * Find Zeenea files. A Zeenea file is a file with the extension.
   *
   * @param ctx Tracing context.
   * @return A list of zeenea file references.
   */
  public List<FileRef> findZeeneaFiles(TracingContext ctx) {
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

  public static FileFinder create(ConnectionConfiguration configuration, String defaultExtension) {
    // Get file path for the configuration.
    var path = configuration.getPath(Configuration.PATH_CONF);

    // File path is relative to scanner home folder.
    var fullPath = path.isAbsolute() ? path : configuration.getScannerHomeFolder().resolve(path);

    var extension =
        configuration
            .getStringOptional(Configuration.EXTENSION_CONF)
            .map(e -> Strings.ensurePrefix(".", e))
            .orElse(Strings.ensurePrefix(".", defaultExtension + ".json"));

    return new FileFinder(fullPath, extension);
  }
}
