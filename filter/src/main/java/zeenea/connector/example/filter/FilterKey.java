package zeenea.connector.example.filter;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

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

  public static FilterKey text(String name) {
    return new FilterKey(FilterKind.TEXT, name);
  }

  public static FilterKey list(String name) {
    return new FilterKey(FilterKind.LIST, name);
  }

  public @NotNull String name() {
    return name;
  }

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
