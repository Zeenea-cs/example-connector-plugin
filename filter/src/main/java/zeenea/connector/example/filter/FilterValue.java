/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** The value of a field in a filter item. */
public interface FilterValue {
  final UnknownKey unknownKey = new UnknownKey();
  final Unset unset = new Unset();

  /**
   * A jocker value that represents the absence a key in the item.
   *
   * @return The unknownKey value.
   */
  static FilterValue unknownKey() {
    return unknownKey;
  }

  /**
   * A jocker value that represents the absence a value in the item.
   *
   * <p>The difference with unknownKey is that the key was present in the item.
   *
   * @return The unset value.
   */
  static FilterValue unset() {
    return unset;
  }

  /**
   * A text value.
   *
   * @param value The text content of the value.
   * @return A new instance of the text value or {@code unset} if value is {@code null}.
   */
  static FilterValue text(String value) {
    return value != null ? new Text(value) : unset;
  }

  /**
   * A list value.
   *
   * @param value The list of the values.
   * @return A new instance of the value.
   * @throws NullPointerException if value is {@code null}.
   */
  static FilterValue list(Collection<FilterValue> value) {
    return new ValueList(List.copyOf(value));
  }

  /**
   * The kind of the value.
   *
   * @return The kind.
   */
  FilterKind kind();

  /**
   * Tell if a value is empty. Currently, only {@code unset} and {@code unknownKey} are empty. A
   * text with an empty string value is not empty.
   *
   * @return {@code true} if the value is empty.
   */
  boolean isEmpty();

  /**
   * Tell if the value is {@code unset}.
   *
   * @return {@code true} is the value is {@code unset}.
   */
  boolean isUnset();

  /**
   * Tell if the value is {@code unknownKey}.
   *
   * @return {@code true} is the value is {@code unknownKey}.
   */
  boolean isUnknownKey();

  final class UnknownKey implements FilterValue {
    private UnknownKey() {}

    @Override
    public FilterKind kind() {
      return FilterKind.EMPTY;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isUnset() {
      return false;
    }

    @Override
    public boolean isUnknownKey() {
      return true;
    }

    @Override
    public boolean equals(Object that) {
      return that instanceof UnknownKey;
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

  final class Unset implements FilterValue {
    private Unset() {}

    @Override
    public FilterKind kind() {
      return FilterKind.EMPTY;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isUnset() {
      return true;
    }

    @Override
    public boolean isUnknownKey() {
      return false;
    }

    @Override
    public boolean equals(Object that) {
      return that instanceof Unset;
    }

    @Override
    public int hashCode() {
      return 1;
    }
  }

  final class Text implements FilterValue {
    private final String value;

    private Text(String value) {
      this.value = Objects.requireNonNull(value);
    }

    @Override
    public FilterKind kind() {
      return FilterKind.TEXT;
    }

    public String value() {
      return value;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean isUnset() {
      return false;
    }

    @Override
    public boolean isUnknownKey() {
      return false;
    }

    @Override
    public boolean equals(Object that) {
      if (this == that) return true;
      if (!(that instanceof Text)) return false;
      return this.value.equals(((Text) that).value);
    }
  }

  final class ValueList implements FilterValue {
    private final List<FilterValue> valueList;

    @Override
    public FilterKind kind() {
      return FilterKind.LIST;
    }

    private ValueList(List<FilterValue> valueList) {
      this.valueList = Objects.requireNonNull(valueList);
    }

    public List<FilterValue> value() {
      return valueList;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean isUnset() {
      return false;
    }

    @Override
    public boolean isUnknownKey() {
      return false;
    }

    @Override
    public boolean equals(Object that) {
      if (this == that) return true;
      if (!(that instanceof ValueList)) return false;
      return this.valueList.equals(((ValueList) that).valueList);
    }
  }
}
