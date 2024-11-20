package zeenea.connector.example.filter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zeenea.connector.example.filter.FilterValue.Text;
import zeenea.connector.example.filter.FilterValue.Unset;
import zeenea.connector.example.filter.FilterValue.ValueList;

public abstract class Filter {

  public static Filter always() {
    return Constant.ALWAYS;
  }

  public static Filter never() {
    return Constant.NEVER;
  }

  public static Filter constant(boolean result) {
    return result ? Constant.ALWAYS : Constant.NEVER;
  }

  public static Filter isNull(FilterKey key) {
    return new IsNull(key);
  }

  public static Filter isEqualTo(FilterKey key, String value) {
    return new IsEqualTo(key, FilterValue.text(value));
  }

  public static Filter startsWith(FilterKey key, String value) {
    return new Starts(key, value);
  }

  public static Filter endsWith(FilterKey key, String value) {
    return new Ends(key, value);
  }

  public static Filter contains(FilterKey key, String value) {
    return new Contains(key, value);
  }

  public static Filter in(FilterKey key, Set<String> values) {
    return new In(key, values);
  }

  public static Filter in(FilterKey key, String... values) {
    return new In(key, Set.of(values));
  }

  public static Filter all(FilterKey key, FilterKey itemKey, Filter filter) {
    if (!filter.keys().contains(itemKey)) return filter;
    return new AllMatch(key, itemKey, filter);
  }

  public static Filter any(FilterKey key, FilterKey itemKey, Filter filter) {
    if (filter.equals(Constant.NEVER)) return Constant.NEVER;
    return new AnyMatch(key, itemKey, filter);
  }

  public static Filter glob(FilterKey key, String glob) {
    return new MatchesGlob(key, glob);
  }

  public static Filter regex(FilterKey key, Pattern pattern) {
    return new MatchesRegex(key, pattern);
  }

  public static Filter not(Filter filter) {
    if (filter instanceof Not) return ((Not) filter).filter;
    return new Not(filter);
  }

  public static Filter and(Filter filter1, Filter filter2) {
    if (filter1.equals(Constant.ALWAYS)) return filter2;
    if (filter2.equals(Constant.ALWAYS)) return filter1;
    if (filter1.equals(Constant.NEVER) || filter2.equals(Constant.NEVER)) return Constant.NEVER;
    return new And(filter1, filter2);
  }

  public static Filter or(Filter filter1, Filter filter2) {
    if (filter1.equals(Constant.NEVER)) return filter2;
    if (filter2.equals(Constant.NEVER)) return filter1;
    if (filter1.equals(Constant.ALWAYS) || filter2.equals(Constant.ALWAYS)) return Constant.ALWAYS;
    return new Or(filter1, filter2);
  }

  public abstract Set<FilterKey> keys();

  public abstract boolean matches(FilterItem item);

  public abstract @NotNull Filter withContext(FilterItem item);

  public @NotNull Filter partial(FilterKey... keys) {
    var partial = rewrite(Set.of(keys));
    return partial != null ? partial : always();
  }

  protected abstract @Nullable Filter rewrite(Set<FilterKey> keys);

  @Override
  public final String toString() {
    return display();
  }

  public abstract String display();

  public String displayInverse() {
    return "not (" + display() + ")";
  }

  public static final class Constant extends Filter {
    private static final Constant ALWAYS = new Constant(true);
    private static final Constant NEVER = new Constant(false);

    private final boolean result;

    @Override
    public Set<FilterKey> keys() {
      return Set.of();
    }

    private Constant(boolean result) {
      this.result = result;
    }

    @Override
    public boolean matches(FilterItem item) {
      return result;
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      return this;
    }

    @Override
    protected Filter rewrite(Set<FilterKey> keys) {
      return this;
    }

    @Override
    public String display() {
      return result ? "always" : "never";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Constant)) return false;
      Constant constant = (Constant) o;
      return result == constant.result;
    }

    @Override
    public int hashCode() {
      return Objects.hash(result);
    }
  }

  public static final class IsNull extends Filter {
    private final FilterKey key;

    private IsNull(FilterKey key) {
      this.key = Objects.requireNonNull(key);
    }

    @Override
    public Set<FilterKey> keys() {
      return Set.of(key);
    }

    @Override
    public boolean matches(FilterItem item) {
      return item.get(key).isEmpty();
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      var value = item.get(key);
      if (value.isUnknownKey()) return this;
      if (value.isUnset()) return always();
      return never();
    }

    @Override
    protected @Nullable Filter rewrite(Set<FilterKey> keys) {
      return keys.contains(key) ? this : null;
    }

    @Override
    public String display() {
      return key.name() + " is null";
    }

    @Override
    public String displayInverse() {
      return key.name() + " is not null";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof IsNull)) return false;
      IsNull isNull = (IsNull) o;
      return Objects.equals(key, isNull.key);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key);
    }
  }

  public static final class IsEqualTo extends Filter {
    private final FilterKey key;
    private final FilterValue value;

    private IsEqualTo(FilterKey key, FilterValue value) {
      if (!key.kind().accepts(value.kind()))
        throw new IllegalArgumentException(
            key + "(" + key.kind() + ") can't be equal to a " + value.kind());
      this.key = key;
      this.value = value;
    }

    @Override
    public Set<FilterKey> keys() {
      return Set.of(key);
    }

    @Override
    public boolean matches(FilterItem item) {
      return item.get(key).equals(value);
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      var itemValue = item.get(key);
      if (itemValue.isUnknownKey()) return this;
      return constant(itemValue.equals(value));
    }

    @Override
    protected @Nullable Filter rewrite(Set<FilterKey> keys) {
      return keys.contains(key) ? this : null;
    }

    @Override
    public String display() {
      return key.name() + " = " + toLiteral(value);
    }

    @Override
    public String displayInverse() {
      return key.name() + " != " + toLiteral(value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof IsEqualTo)) return false;
      IsEqualTo that = (IsEqualTo) o;
      return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }
  }

  abstract static class TextFilter extends Filter {
    protected final FilterKey key;

    protected TextFilter(FilterKey key) {
      if (key.kind() != FilterKind.TEXT)
        throw new IllegalArgumentException(
            "Invalid key type, expected TEXT: " + key + "(" + key.kind() + ")");
      this.key = key;
    }

    @Override
    public Set<FilterKey> keys() {
      return Set.of(key);
    }

    protected abstract boolean matchesValues(String text);

    @Override
    public boolean matches(FilterItem item) {
      var value = item.get(key);
      if (value instanceof Text) {
        return matchesValues(((Text) value).value());
      }
      return false;
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      var value = item.get(key);
      if (value.isUnknownKey()) return this;
      if (value instanceof Text && matchesValues(((Text) value).value())) {
        return always();
      }
      return never();
    }

    @Override
    protected @Nullable Filter rewrite(Set<FilterKey> keys) {
      return keys.contains(key) ? this : null;
    }
  }

  public static final class Starts extends TextFilter {
    private final String value;

    private Starts(FilterKey key, String value) {
      super(key);
      this.value = value;
    }

    @Override
    protected boolean matchesValues(String text) {
      return text.startsWith(value);
    }

    @Override
    public String display() {
      return key.name() + " starts with " + toLiteral(value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Starts)) return false;
      Starts that = (Starts) o;
      return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }
  }

  public static final class Ends extends TextFilter {
    private final String value;

    private Ends(FilterKey key, String value) {
      super(key);
      this.value = value;
    }

    @Override
    protected boolean matchesValues(String text) {
      return text.endsWith(value);
    }

    @Override
    public String display() {
      return key.name() + " ends with " + toLiteral(value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Ends)) return false;
      Ends that = (Ends) o;
      return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }
  }

  public static final class Contains extends TextFilter {
    private final String value;

    private Contains(FilterKey key, String value) {
      super(key);
      this.value = value;
    }

    @Override
    protected boolean matchesValues(String text) {
      return text.contains(value);
    }

    @Override
    public String display() {
      return key.name() + " contains " + toLiteral(value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Contains)) return false;
      Contains that = (Contains) o;
      return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, value);
    }
  }

  public static final class In extends TextFilter {
    private final Set<String> values;

    private In(FilterKey key, Set<String> values) {
      super(key);
      this.values = values;
    }

    @Override
    protected boolean matchesValues(String text) {
      return values.contains(text);
    }

    @Override
    public String display() {
      return key.name()
          + " in ("
          + values.stream().map(Filter::toLiteral).collect(Collectors.joining(", "))
          + ')';
    }

    @Override
    public String displayInverse() {
      return key.name()
          + " not in ("
          + values.stream().map(Filter::toLiteral).collect(Collectors.joining(", "))
          + ')';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof In)) return false;
      In that = (In) o;
      return Objects.equals(key, that.key) && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, values);
    }
  }

  public static final class MatchesGlob extends TextFilter {
    private final String glob;
    private final Pattern pattern;

    public MatchesGlob(FilterKey key, String glob) {
      super(key);
      this.glob = glob;
      this.pattern = Pattern.compile(Glob.toRegex(glob));
    }

    @Override
    protected boolean matchesValues(String text) {
      return pattern.matcher(text).matches();
    }

    public String glob() {
      return glob;
    }

    public Pattern pattern() {
      return pattern;
    }

    @Override
    public String display() {
      return key.name() + " ~ " + Filter.toLiteral(glob);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MatchesGlob)) return false;
      MatchesGlob that = (MatchesGlob) o;
      return Objects.equals(key, that.key) && Objects.equals(glob, that.glob);
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, glob);
    }
  }

  public static final class MatchesRegex extends TextFilter {
    private final Pattern pattern;

    public MatchesRegex(FilterKey key, Pattern pattern) {
      super(key);
      this.pattern = pattern;
    }

    public Pattern pattern() {
      return pattern;
    }

    @Override
    protected boolean matchesValues(String text) {
      return pattern.matcher(text).matches();
    }

    @Override
    public String display() {
      return key.name() + " ~ /" + pattern.pattern() + '/' + literalFlags(pattern);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MatchesRegex)) return false;
      MatchesRegex that = (MatchesRegex) o;
      return Objects.equals(key, that.key)
          && Objects.equals(pattern.pattern(), that.pattern.pattern())
          && pattern.flags() == that.pattern.flags();
    }

    @Override
    public int hashCode() {
      return Objects.hash(key, pattern.pattern(), pattern.flags());
    }
  }

  public static final class Not extends Filter {
    private final Filter filter;

    public Not(Filter filter) {
      this.filter = Objects.requireNonNull(filter);
    }

    @Override
    public Set<FilterKey> keys() {
      return filter.keys();
    }

    @Override
    public boolean matches(FilterItem item) {
      return !filter.matches(item);
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      Filter filterWithContext = filter.withContext(item);
      if (filterWithContext.equals(Constant.ALWAYS)) return never();
      if (filterWithContext.equals(Constant.NEVER)) return always();
      return Filter.not(filterWithContext);
    }

    @Override
    protected @Nullable Filter rewrite(Set<FilterKey> keys) {
      var rewritten = filter.rewrite(keys);
      return rewritten != null ? Filter.not(rewritten) : null;
    }

    @Override
    public String display() {
      return filter.displayInverse();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Not)) return false;
      Not not = (Not) o;
      return Objects.equals(filter, not.filter);
    }

    @Override
    public int hashCode() {
      return Objects.hash(filter);
    }
  }

  public abstract static class ListFilter extends Filter {
    protected final FilterKey key;
    protected final FilterKey elementKey;
    protected final Filter filter;

    public ListFilter(FilterKey key, FilterKey elementKey, Filter filter) {
      if (key.kind() != FilterKind.LIST)
        throw new IllegalArgumentException(
            "Invalid key type, expected LIST: " + key + "(" + key.kind() + ")");
      if (elementKey.kind() != FilterKind.TEXT)
        throw new IllegalArgumentException(
            "Invalid element key type, expected TEXT: "
                + elementKey
                + "("
                + elementKey.kind()
                + ")");

      this.key = key;
      this.elementKey = elementKey;
      this.filter = filter;
    }

    @Override
    public final Set<FilterKey> keys() {
      var allKeys = new HashSet<>(filter.keys());
      allKeys.remove(elementKey);
      allKeys.add(key);
      return allKeys;
    }

    @Override
    public final boolean matches(FilterItem item) {
      var value = item.get(key);
      return value instanceof ValueList && matchesValues(item, (ValueList) value);
    }

    protected abstract boolean matchesValues(FilterItem item, ValueList valueList);

    protected final boolean elementMatches(FilterItem item, FilterValue elementValue) {
      try {
        item.push(elementKey, elementValue);
        return filter.matches(item);
      } finally {
        item.pop(elementKey);
      }
    }

    protected abstract Filter withFilter(Filter newFilter);

    @Override
    public final @NotNull Filter withContext(FilterItem item) {
      var contextFilter = filter.withContext(item);
      var candidate = withFilter(contextFilter);
      if (candidate.keys().stream().noneMatch(k -> item.get(k).isUnknownKey()))
        return candidate.matches(item) ? Constant.ALWAYS : Constant.NEVER;
      return candidate;
    }

    @Override
    protected final @Nullable Filter rewrite(Set<FilterKey> keys) {
      if (!keys.contains(key)) return null;
      var elementKeys = new HashSet<>(keys);
      elementKeys.add(elementKey);
      Filter rewrittenFilter = filter.rewrite(elementKeys);
      if (rewrittenFilter == null) return null;
      return withFilter(rewrittenFilter);
    }
  }

  public static final class AllMatch extends ListFilter {
    public AllMatch(FilterKey key, FilterKey elementKey, Filter filter) {
      super(key, elementKey, filter);
    }

    protected boolean matchesValues(FilterItem item, ValueList valueList) {
      return valueList.value().stream()
          .allMatch(elementValue -> elementMatches(item, elementValue));
    }

    @Override
    protected Filter withFilter(Filter newFilter) {
      return Filter.all(key, elementKey, newFilter);
    }

    @Override
    public String display() {
      return "all " + elementKey.name() + " in " + key.name() + " match (" + filter.display() + ")";
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof AllMatch)) return false;
      AllMatch all = (AllMatch) o;
      return Objects.equals(key, all.key)
          && Objects.equals(elementKey, all.elementKey)
          && filter.equals(all.filter);
    }
  }

  public static final class AnyMatch extends ListFilter {
    public AnyMatch(FilterKey key, FilterKey elementKey, Filter filter) {
      super(key, elementKey, filter);
    }

    protected boolean matchesValues(FilterItem item, ValueList valueList) {
      return valueList.value().stream()
          .anyMatch(elementValue -> elementMatches(item, elementValue));
    }

    @Override
    protected Filter withFilter(Filter newFilter) {
      return Filter.any(key, elementKey, newFilter);
    }

    @Override
    public String display() {
      return "any " + elementKey.name() + " in " + key.name() + " match (" + filter.display() + ")";
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof AnyMatch)) return false;
      AnyMatch any = (AnyMatch) o;
      return Objects.equals(key, any.key)
          && Objects.equals(elementKey, any.elementKey)
          && filter.equals(any.filter);
    }
  }

  public static final class And extends Filter {
    private final Filter filter1;
    private final Filter filter2;

    public And(Filter filter1, Filter filter2) {
      this.filter1 = Objects.requireNonNull(filter1);
      this.filter2 = Objects.requireNonNull(filter2);
    }

    @Override
    public Set<FilterKey> keys() {
      var set = new HashSet<>(filter1.keys());
      set.addAll(filter2.keys());
      return set;
    }

    @Override
    public boolean matches(FilterItem item) {
      return filter1.matches(item) && filter2.matches(item);
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      return Filter.and(filter1.withContext(item), filter2.withContext(item));
    }

    @Override
    protected @Nullable Filter rewrite(Set<FilterKey> keys) {
      var rewrite1 = filter1.rewrite(keys);
      var rewrite2 = filter2.rewrite(keys);
      if (rewrite1 != null && rewrite2 != null) return Filter.and(rewrite1, rewrite2);
      if (rewrite1 != null) return rewrite1;
      return rewrite2;
    }

    @Override
    public String display() {
      return "(" + filter1.display() + ") and (" + filter2.display() + ")";
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof And)) return false;
      And and = (And) o;
      return Objects.equals(filter1, and.filter1) && Objects.equals(filter2, and.filter2);
    }

    @Override
    public int hashCode() {
      return Objects.hash(filter1, filter2);
    }
  }

  public static final class Or extends Filter {
    private final Filter filter1;
    private final Filter filter2;

    public Or(Filter filter1, Filter filter2) {
      this.filter1 = Objects.requireNonNull(filter1);
      this.filter2 = Objects.requireNonNull(filter2);
    }

    @Override
    public Set<FilterKey> keys() {
      var set = new HashSet<>(filter1.keys());
      set.addAll(filter2.keys());
      return set;
    }

    @Override
    public boolean matches(FilterItem item) {
      return filter1.matches(item) || filter2.matches(item);
    }

    @Override
    public @NotNull Filter withContext(FilterItem item) {
      return Filter.or(filter1.withContext(item), filter2.withContext(item));
    }

    @Override
    protected @Nullable Filter rewrite(Set<FilterKey> keys) {
      var rewrite1 = filter1.rewrite(keys);
      var rewrite2 = filter2.rewrite(keys);
      if (rewrite1 != null && rewrite2 != null) return Filter.or(rewrite1, rewrite2);
      return null;
    }

    @Override
    public String display() {
      return "(" + filter1.display() + ") or (" + filter2.display() + ")";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Or)) return false;
      Or and = (Or) o;
      return Objects.equals(filter1, and.filter1) && Objects.equals(filter2, and.filter2);
    }

    @Override
    public int hashCode() {
      return Objects.hash(filter1, filter2);
    }
  }

  static String fromLiteral(String text) {
    var sb = new StringBuilder();
    for (int i = 1, n = text.length() - 1; i < n; ++i) {
      char c = text.charAt(i);
      if (c == '\\' && i < n - 1) {
        sb.append(text.charAt(++i));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static String toLiteral(String text) {
    var sb = new StringBuilder();
    sb.append('\'');
    for (int i = 0, n = text.length(); i < n; ++i) {
      int c = text.charAt(i);
      if (c == '\'' || c == '\\') sb.append('\\');
      sb.append((char) c);
    }
    sb.append('\'');
    return sb.toString();
  }

  private static String toLiteral(FilterValue value) {
    if (value instanceof ValueList)
      return ((ValueList) value)
          .value().stream().map(Filter::toLiteral).collect(Collectors.joining(", ", "(", ")"));
    if (value instanceof Text) return toLiteral(((Text) value).value());
    if (value instanceof Unset) return "unset";
    return "unknown";
  }

  private static String literalFlags(Pattern pattern) {
    var flags = pattern.flags();
    var sb = new StringBuilder(7);
    if ((flags & Pattern.UNIX_LINES) != 0) sb.append("d");
    if ((flags & Pattern.CASE_INSENSITIVE) != 0) sb.append("i");
    if ((flags & Pattern.COMMENTS) != 0) sb.append("x");
    if ((flags & Pattern.MULTILINE) != 0) sb.append("m");
    if ((flags & Pattern.DOTALL) != 0) sb.append("s");
    if ((flags & Pattern.UNICODE_CASE) != 0) sb.append("u");
    if ((flags & Pattern.UNICODE_CHARACTER_CLASS) != 0) sb.append("U");
    return sb.toString();
  }

  static int flags(String flagSpecs) {
    int flags = 0;
    for (int i = 0, n = flagSpecs.length(); i < n; ++i) {
      char c = flagSpecs.charAt(i);
      switch (c) {
        case 'd':
          flags |= Pattern.UNIX_LINES;
          break;
        case 'i':
          flags |= Pattern.CASE_INSENSITIVE;
          break;
        case 'x':
          flags |= Pattern.COMMENTS;
          break;
        case 'm':
          flags |= Pattern.MULTILINE;
          break;
        case 's':
          flags |= Pattern.DOTALL;
          break;
        case 'u':
          flags |= Pattern.UNICODE_CASE;
          break;
        case 'U':
          flags |= Pattern.UNICODE_CHARACTER_CLASS;
          break;
      }
    }
    return flags;
  }
}
