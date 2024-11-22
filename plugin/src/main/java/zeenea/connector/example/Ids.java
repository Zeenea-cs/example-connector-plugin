package zeenea.connector.example;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import zeenea.connector.common.ItemIdentifier;

public class Ids {

  public static Supplier<String> log(ItemIdentifier itemId) {
    return () ->
        itemId.getIdentificationProperties().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("/", "/", ""));
  }

  public static Supplier<String> logLabels(List<String> labels) {
    return () -> String.join("/", labels);
  }
}
