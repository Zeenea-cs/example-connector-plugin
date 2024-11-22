package zeenea.connector.example.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRef;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.Strings;
import zeenea.connector.example.log.TracingContext;

public class Json {
  private static final SimpleLogger log = SimpleLogger.of(Json.class);

  private static final JsonMapper JSON_MAPPER =
      JsonMapper.builder()
          .addModule(new Jdk8Module())
          .addModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
          .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .build();

  public static <T> T readFromFile(Path path, Class<T> klass) {
    try {
      return JSON_MAPPER.readValue(path.toFile(), klass);
    } catch (JsonProcessingException e) {
      throw new JsonParsingException(
          String.format(
              "Failed parsing file '%s' to '%s': %s", path, klass.getName(), e.getMessage()),
          e);
    } catch (IOException e) {
      throw new JsonParsingException(
          String.format(
              "Failed reading file '%s' to '%s': %s", path, e.getMessage(), klass.getName()),
          e);
    }
  }

  /**
   * Read the content of a file.
   *
   * <p>If the file fails, the exception is caught, logged and an empty stream is returned.
   *
   * @param ctx Tracing context.
   * @param fileRef The reference of the file.
   * @param rootClass The root class.
   * @param items A function to get the list of elements from the root.
   * @param <R> The root type.
   * @param <E> The element type.
   * @return The stream of the file items.
   */
  public static <R, E> Stream<FileItem<E>> readItems(
          TracingContext ctx, FileRef fileRef, Class<R> rootClass, Function<R, List<E>> items) {
    // Extract the connector case by convention on the root class name.
    var env = Strings.chopSuffix(rootClass.getSimpleName(), "Root").toLowerCase();
    try {
      log.entry("example_" + env + "_read_file")
              .context(ctx)
              .with("path", fileRef.getRelativePath())
              .info();

      return Stream.ofNullable(Json.readFromFile(fileRef.getPath(), rootClass))
              .flatMap(root -> items.apply(root).stream())
              .map(p -> new FileItem<>(p, fileRef));

    } catch (JsonParsingException e) {
      log.entry("example_" + env + "_read_file_failure")
              .context(ctx)
              .with("path", fileRef.getPath())
              .error(e);
      return Stream.empty();
    }
  }
}
