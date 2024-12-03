package zeenea.connector.example;

import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;

public class TestPath {
  public static Path parentFolder(String resource) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    var resourceUrl = cl.getResource(resource);
    if (resourceUrl == null) Assertions.fail("Could not find resource: " + resource);
    try {
      return Path.of(resourceUrl.toURI());
    } catch (URISyntaxException e) {
      return Assertions.fail("Invalid URI: " + resourceUrl);
    }
  }
}
