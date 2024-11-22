package zeenea.connector.example.filter;

import java.util.Set;

/** Parser of filter specification. */
public class FilterParser {
  private final FilterKeyDictionary keyDictionary;

  private FilterParser(Set<FilterKey> keySet) {
    this.keyDictionary = FilterKeyDictionary.of(keySet);
  }

  /** Create a parser that accept the given keys. */
  public static FilterParser of(FilterKey... keys) {
    return new FilterParser(Set.of(keys));
  }

  /** Create a parser that accept the given set of keys. */
  public static FilterParser of(Set<FilterKey> keySet) {
    return new FilterParser(keySet);
  }

  /**
   * Parse the filter specification.
   *
   * @param filterSpec The filter specification.
   * @return A new filter.
   * @throws FilterParsingException If the specification is not valid or if the parsing fails.
   */
  public Filter parse(String filterSpec) {
    try {
      if (filterSpec == null || filterSpec.isEmpty()) return Filter.always();
      RawFilterParser rawParser = new RawFilterParser(filterSpec);
      rawParser.keyDic = keyDictionary;
      return rawParser.filter();
    } catch (TokenMgrException | ParseException e) {
      throw new FilterParsingException(e.getMessage(), e);
    }
  }
}
