package zeenea.connector.example.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FilterItem {
  private final Map<FilterKey, FilterValue> values;
  private final LinkedList<FilterKeyValue> localValues = new LinkedList<>();

  private FilterItem(Map<FilterKey, FilterValue> values) {
    this.values = values;
  }

  public static FilterItem of(Map<FilterKey, FilterValue> values) {
    return new FilterItem(Map.copyOf(values));
  }

  public static FilterItem of(List<FilterKeyValue> values) {
    return new FilterItem(
        values.stream()
            .collect(Collectors.toUnmodifiableMap(FilterKeyValue::key, FilterKeyValue::value)));
  }

  public static FilterItem of(FilterKeyValue... values) {
    return new FilterItem(
        Stream.of(values)
            .collect(Collectors.toUnmodifiableMap(FilterKeyValue::key, FilterKeyValue::value)));
  }

  public void push(FilterKey key, FilterValue value) {
    push(FilterKeyValue.of(key, value));
  }

  public void push(FilterKeyValue keyValue) {
    localValues.push(keyValue);
  }

  public void pop(FilterKey key) {
    FilterKeyValue removed = localValues.pop();
    if (!removed.key().equals(key))
      throw new IllegalStateException(
          "Invalid key removed. Expected " + key + " removed " + removed.key());
  }

  public FilterValue get(FilterKey key) {
    return localValues.stream()
        .filter(kv -> kv.key().equals(key))
        .map(FilterKeyValue::value)
        .findFirst()
        .orElseGet(() -> values.getOrDefault(key, FilterValue.unknownKey));
  }
}
