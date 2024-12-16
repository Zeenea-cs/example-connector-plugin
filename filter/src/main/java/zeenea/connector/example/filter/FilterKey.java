/*
 * This work is marked with CC0 1.0 Universal.
 * To view a copy of this license, visit https://creativecommons.org/publicdomain/zero/1.0/
 *
 */

package zeenea.connector.example.filter;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * A key identify a field in a filter item.
 *
 * <p>A key has a name that musr contain only characters, digits and underscore ("_").
 *
 * <p>A key can be of two kinds:
 *
 * <dl>
 *   <dt>text
 *   <dd>The value is a single text value.
 *   <dt>list
 *   <dd>The value is a list of text values.
 * </dl>
 */
public final class FilterKey {
  private static final Pattern NAME_PATTERN = Pattern.compile("[_a-zA-Z][_a-zA-Z0-9]+");
  private final @NotNull String name;
  private final @NotNull FilterKind kind;

  private FilterKey(@NotNull FilterKind kind, @NotNull String name) {
    this.kind = requireNonNull(kind, "kind");
    if (!NAME_PATTERN.matcher(name).matches()) {
      throw new IllegalArgumentException(
          "Invalid filter key name '" + name + "' should match /" + NAME_PATTERN.pattern() + "/");
    }
    this.name = name;
  }

  /**
   * Create a new text key.
   *
   * @param name The name of the key.
   * @return A new instance of the key.
   */
  public static FilterKey text(String name) {
    return new FilterKey(FilterKind.TEXT, name);
  }

  /**
   * Create a new list key.
   *
   * @param name The name of the key.
   * @return A new instance of the key.
   */
  public static FilterKey list(String name) {
    return new FilterKey(FilterKind.LIST, name);
  }

  /**
   * The name of the key.
   *
   * @return the name.
   */
  public @NotNull String name() {
    return name;
  }

  /**
   * The kind of the key.
   *
   * @return the kind.
   */
  public @NotNull FilterKind kind() {
    return kind;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FilterKey)) return false;
    FilterKey filterKey = (FilterKey) o;
    return Objects.equals(name, filterKey.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FilterKey.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("kind='" + kind + "'")
        .toString();
  }
}
