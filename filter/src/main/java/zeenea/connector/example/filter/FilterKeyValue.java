/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/** A couple of a key and a value. */
public final class FilterKeyValue {
  private final FilterKey key;
  private final FilterValue value;

  private FilterKeyValue(FilterKey key, FilterValue value) {
    this.key = Objects.requireNonNull(key);
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Create a key-value instance.
   *
   * @param key The key.
   * @param value The value.
   * @return the new instance.
   */
  public static FilterKeyValue of(FilterKey key, FilterValue value) {
    return new FilterKeyValue(key, value);
  }

  /**
   * A convenient way to create a {@code FilterKeyValue} for a text key.
   *
   * @param key The key.
   * @param value The value.
   * @return the new instance.
   */
  public static FilterKeyValue text(FilterKey key, String value) {
    return of(key, FilterValue.text(value));
  }

  /**
   * A convenient way to create a {@code FilterKeyValue} for a list key.
   *
   * @param key The key.
   * @param value The value.
   * @return the new instance.
   */
  public static FilterKeyValue list(FilterKey key, Collection<FilterValue> value) {
    return new FilterKeyValue(key, FilterValue.list(value));
  }

  /**
   * A convenient way to create a {@code FilterKeyValue} for a list key from an array of strings.
   *
   * @param key The key.
   * @param value The value.
   * @return the new instance.
   */
  public static FilterKeyValue textList(FilterKey key, String... value) {
    return list(key, Arrays.stream(value).map(FilterValue::text).collect(Collectors.toList()));
  }

  /**
   * A convenient way to create a {@code FilterKeyValue} for a list key from a collection of
   * strings.
   *
   * @param key The key.
   * @param value The value.
   * @return the new instance.
   */
  public static FilterKeyValue textList(FilterKey key, Collection<String> value) {
    return list(key, value.stream().map(FilterValue::text).collect(Collectors.toList()));
  }

  /**
   * The key.
   *
   * @return the key.
   */
  public FilterKey key() {
    return key;
  }

  /**
   * The value.
   *
   * @return The value.
   */
  public FilterValue value() {
    return value;
  }
}
