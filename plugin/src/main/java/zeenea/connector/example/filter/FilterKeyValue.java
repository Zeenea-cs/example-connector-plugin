package zeenea.connector.example.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FilterKeyValue {
  private final FilterKey key;
  private final FilterValue value;

  private FilterKeyValue(FilterKey key, FilterValue value) {
    this.key = Objects.requireNonNull(key);
    this.value = Objects.requireNonNull(value);
  }

  public static FilterKeyValue of(FilterKey key, FilterValue value) {
    return new FilterKeyValue(key, value);
  }

  public static FilterKeyValue list(FilterKey key, Collection<FilterValue> value) {
    return new FilterKeyValue(key, FilterValue.list(value));
  }

  public static FilterKeyValue text(FilterKey key, String value) {
    return of(key, FilterValue.text(value));
  }

  public static FilterKeyValue textList(FilterKey key, String... value) {
    return list(key, Arrays.stream(value).map(FilterValue::text).collect(Collectors.toList()));
  }

  public static FilterKeyValue textList(FilterKey key, Collection<String> value) {
    return list(key, value.stream().map(FilterValue::text).collect(Collectors.toList()));
  }

  public FilterKey key() {
    return key;
  }

  public FilterValue value() {
    return value;
  }
}
