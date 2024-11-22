package zeenea.connector.example.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** The container of the data tested by the filter. */
public final class FilterItem {
  private final Map<FilterKey, FilterValue> values;
  private final LinkedList<FilterKeyValue> localValues = new LinkedList<>();

  private FilterItem(Map<FilterKey, FilterValue> values) {
    this.values = values;
  }

  /**
   * Create a new item from a map of key-values.
   *
   * @param values The values.
   * @return A new item.
   */
  public static FilterItem of(Map<FilterKey, FilterValue> values) {
    return new FilterItem(Map.copyOf(values));
  }

  /**
   * Create a new item from a list of key-values.
   *
   * @param values The values.
   * @return A new item.
   */
  public static FilterItem of(List<FilterKeyValue> values) {
    return new FilterItem(
        values.stream()
            .collect(Collectors.toUnmodifiableMap(FilterKeyValue::key, FilterKeyValue::value)));
  }

  /**
   * Create a new item from an array of key-values.
   *
   * @param values The values.
   * @return A new item.
   */
  public static FilterItem of(FilterKeyValue... values) {
    return new FilterItem(
        Stream.of(values)
            .collect(Collectors.toUnmodifiableMap(FilterKeyValue::key, FilterKeyValue::value)));
  }

  /**
   * Add a temporary value in the item.
   *
   * @param key The temporary key.
   * @param value The temporary value.
   */
  void push(FilterKey key, FilterValue value) {
    push(FilterKeyValue.of(key, value));
  }

  /**
   * Add a temporary value in the item.
   *
   * @param keyValue The temporary key and value.
   */
  void push(FilterKeyValue keyValue) {
    localValues.push(keyValue);
  }

  /**
   * Remove a temporary value.
   *
   * <p>Value should be removed in the same order they have been added. The {@code key} parameter
   * with be compared with the top value and an {@code IllegalStateException} will be thrown if they
   * don't match.
   *
   * @param key The expected key
   * @throws IllegalStateException if the key doesn't
   * @throws java.util.NoSuchElementException if there is no temporary value.
   */
  void pop(FilterKey key) {
    FilterKeyValue removed = localValues.pop();
    if (!removed.key().equals(key))
      throw new IllegalStateException(
          "Invalid key removed. Expected " + key + " removed " + removed.key());
  }

  /**
   * Get the value of a given key.
   *
   * @param key The key.
   * @return The value of the key or {@code FilterValue#unknownKey} if the key doesn't exists.
   */
  public FilterValue get(FilterKey key) {
    return localValues.stream()
        .filter(kv -> kv.key().equals(key))
        .map(FilterKeyValue::value)
        .findFirst()
        .orElseGet(() -> values.getOrDefault(key, FilterValue.unknownKey));
  }
}
