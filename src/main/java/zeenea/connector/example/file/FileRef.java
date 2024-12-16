/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.file;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class FileRef {
  private final Path path;
  private final Path workDir;

  public FileRef(Path path, Path workDir) {
    this.path = Objects.requireNonNull(path);
    this.workDir = Objects.requireNonNull(workDir);
  }

  public Path getPath() {
    return path;
  }

  public Path getWorkDir() {
    return workDir;
  }

  public String getRelativePath() {
    var rel = workDir.relativize(path);
    return StreamSupport.stream(rel.spliterator(), false)
        .map(Path::toString)
        .collect(Collectors.joining("/"));
  }
}
