package zeenea.connector.example.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import zeenea.connector.example.file.FileItem;
import zeenea.connector.example.file.FileRef;
import zeenea.connector.example.log.SimpleLogger;
import zeenea.connector.example.log.Strings;
import zeenea.connector.example.log.TracingContext;

public class Json {
  private static final SimpleLogger log = SimpleLogger.of(Json.class);

  private static final JsonMapper MAPPER =
      JsonMapper.builder()
          .addModule(new Jdk8Module())
          .addModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
          .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
          .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .build();

  /**
   * Read the content of a file.
   *
   * <p>If the file fails, the exception is caught, logged and an empty stream is returned.
   *
   * @param ctx Tracing context.
   * @param fileRef The reference of the file.
   * @param klass The item class.
   * @param <T> The element type.
   * @return The stream of the file items.
   */
  public static <T extends JsonItem> Stream<FileItem<T>> readItems(
      TracingContext ctx, FileRef fileRef, Class<T> klass) {
    // Extract the connector case by convention on the root class name.
    var env = Strings.removePrefix(klass.getSimpleName(), "Json").toLowerCase();

    log.entry("example_" + env + "_read_file")
        .context(ctx)
        .with("path", fileRef.getRelativePath())
        .info();

    try (var input = MAPPER.createParser(fileRef.getPath().toFile())) {
      MappingIterator<T> iterator = MAPPER.readValues(input, klass);
      Spliterator<T> spliterator =
          Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.NONNULL);
      return StreamSupport.stream(spliterator, false).map(p -> new FileItem<>(p, fileRef));

    } catch (IOException | RuntimeException e) {
      log.entry("example_" + env + "_read_file_failure")
          .context(ctx)
          .with("path", fileRef.getPath())
          .error(e);
      return Stream.empty();
    }
  }
}
