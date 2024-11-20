package zeenea.connector.example.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;

public class Json {
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
}
