package zeenea.connector.example.filter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface FilterValue {
  final UnknownKey unknownKey = new UnknownKey();
  final Unset unset = new Unset();

  static FilterValue unknownKey() {
    return unknownKey;
  }

  static FilterValue unset() {
    return unset;
  }

  static FilterValue text(String value) {
    return value != null ? new Text(value) : unset;
  }

  static FilterValue list(Collection<FilterValue> value) {
    return new ValueList(List.copyOf(value));
  }

  FilterKind kind();

  boolean isEmpty();

  boolean isUnset();

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
